package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.platform.MiscPlatformHelper;
import dev.latvian.mods.kubejs.script.ConsoleLine;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.EcmaError;
import dev.latvian.mods.rhino.RhinoException;
import dev.latvian.mods.rhino.WrappedException;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ConsoleJS {
	public static ConsoleJS STARTUP;
	public static ConsoleJS SERVER;
	public static ConsoleJS CLIENT;

	private record LogFunc(ConsoleJS console, LogType type) implements Consumer<ConsoleLine> {
		@Override
		public void accept(ConsoleLine line) {
			type.callback.accept(console.logger, line.getText());

			if (console.capturingErrors) {
				if (type == LogType.ERROR) {
					console.errors.add(line);
				} else if (type == LogType.WARN) {
					console.warnings.add(line);
				}
			}
		}
	}

	public static ConsoleJS getCurrent(ConsoleJS def) {
		Context cx = ScriptManager.getCurrentContext();
		return cx == null ? def : getCurrent(cx);
	}

	public static ConsoleJS getCurrent(@Nullable Context cx) {
		if (cx == null) {
			cx = ScriptManager.getCurrentContext();

			if (cx == null) {
				return STARTUP;
			}
		}

		return cx.getProperty("Console", null);
	}

	private static final Pattern GARBAGE_PATTERN = Pattern.compile("(?:TRANSFORMER|LAYER PLUGIN)/\\w+@[^/]+/");
	private static final Function<String, String> ERROR_REDUCE = s -> {
		if (s.startsWith("java.util.concurrent.ForkJoin") || s.startsWith("jdk.internal.")) {
			return "";
		}

		return GARBAGE_PATTERN.matcher(s).replaceAll("").replace("dev.latvian.mods.", "…");
	};

	public final ScriptType scriptType;
	private transient boolean capturingErrors;
	public final transient Collection<ConsoleLine> errors;
	public final transient Collection<ConsoleLine> warnings;
	private final Logger logger;
	private final Path logFile;
	private String group;
	private boolean muted;
	private boolean debugEnabled;
	private boolean writeToFile;
	private final List<String> writeQueue;
	private final String nameStrip;
	private final Calendar calendar;

	public final Consumer<ConsoleLine> debugLogFunction;
	public final Consumer<ConsoleLine> infoLogFunction;
	public Consumer<ConsoleLine> warnLogFunction;
	public Consumer<ConsoleLine> errorLogFunction;

	public ConsoleJS(ScriptType m, Logger log) {
		this.scriptType = m;
		this.errors = new ConcurrentLinkedDeque<>();
		this.warnings = new ConcurrentLinkedDeque<>();
		this.logger = log;
		this.logFile = m.getLogFile();
		this.group = "";
		this.muted = false;
		this.debugEnabled = false;
		this.writeToFile = true;
		this.writeQueue = new LinkedList<>();
		this.nameStrip = scriptType.name + "_scripts:";
		this.calendar = Calendar.getInstance();

		this.debugLogFunction = new LogFunc(this, LogType.DEBUG);
		this.infoLogFunction = new LogFunc(this, LogType.INFO);
		this.warnLogFunction = new LogFunc(this, LogType.WARN);
		this.errorLogFunction = new LogFunc(this, LogType.ERROR);
	}

	public Logger getLogger() {
		return logger;
	}

	protected boolean shouldPrint() {
		return !muted;
	}

	public void setMuted(boolean m) {
		muted = m;
	}

	public boolean getMuted() {
		return muted;
	}

	public void setDebugEnabled(boolean m) {
		debugEnabled = m;
	}

	public boolean getDebugEnabled() {
		return debugEnabled;
	}

	public synchronized void setWriteToFile(boolean m) {
		writeToFile = m;
	}

	public synchronized boolean getWriteToFile() {
		return writeToFile;
	}

	public synchronized void setCapturingErrors(boolean enabled) {
		capturingErrors = enabled;

		if (DevProperties.get().debugInfo) {
			if (enabled) {
				logger.info("Capturing errors for " + scriptType.name + " scripts enabled");
			} else {
				logger.info("Capturing errors for " + scriptType.name + " scripts disabled");
			}
		}
	}

	public void resetFile() {
		scriptType.executor.execute(() -> {
			try {
				Files.write(logFile, List.of());
			} catch (Exception ex) {
				logger.error("Failed to clear the log file: " + ex);
			}
		});
	}

	private ConsoleLine line(LogType type, Object object, @Nullable Throwable error) {
		var o = UtilsJS.wrap(object, JSObjectType.ANY);

		if (o instanceof Component c) {
			o = c.getString();
		}

		var timestamp = System.currentTimeMillis();
		var line = new ConsoleLine(this, timestamp, o == null || o.getClass().isPrimitive() || o instanceof Boolean || o instanceof String || o instanceof Number || o instanceof WrappedJS ? String.valueOf(o) : (o + " [" + o.getClass().getName() + "]"));
		line.type = type;
		line.group = group;

		if (error instanceof RhinoException ex) {
			line.line = ex.lineNumber();
			line.source = ex.lineSource();

			if (line.source == null) {
				line.source = "";
			}
		}

		if (line.message != null) {
			int lpi = line.message.lastIndexOf('(');

			if (lpi > 0 && line.message.charAt(line.message.length() - 1) == ')') {
				var pe = line.message.substring(lpi + 1, line.message.length() - 1);

				int ci = pe.lastIndexOf('#');

				if (ci > 0) {
					try {
						line.line = Integer.parseInt(pe.substring(ci + 1));
						line.source = pe.substring(0, ci);
						line.message = line.message.substring(0, lpi).trim();
					} catch (Exception e) {
					}
				}
			}
		}

		if (line.line == 0 || line.source.isEmpty()) {
			int[] lineP = {0};
			line.source = Context.getSourcePositionFromStack(scriptType.manager.get().context, lineP);

			if (line.source == null) {
				line.source = "";
			}

			line.line = lineP[0];
		}

		if (!line.source.isEmpty() && line.source.startsWith(nameStrip)) {
			line.source = line.source.substring(nameStrip.length());
		}

		return line;
	}

	private ConsoleLine log(Consumer<ConsoleLine> logFunction, LogType type, @Nullable Throwable error, Object message) {
		if (shouldPrint()) {
			var s = line(type, message, error);
			logFunction.accept(s);

			if (writeToFile) {
				writeToFile(type, s.timestamp, s.getText());
			}

			return s;
		}

		return null;
	}

	public synchronized void writeToFile(LogType type, String line) {
		writeToFile(type, System.currentTimeMillis(), line);
	}

	public synchronized void writeToFile(LogType type, long timestamp, String line) {
		if (!writeToFile || MiscPlatformHelper.get().isDataGen()) {
			return;
		}

		calendar.setTimeInMillis(timestamp);
		var sb = new StringBuilder();

		sb.append('[');
		UtilsJS.appendTimestamp(sb, calendar);
		sb.append(']');
		sb.append(' ');
		sb.append('[');
		sb.append(type);
		sb.append(']');
		sb.append(' ');

		if (type == LogType.ERROR) {
			sb.append('!');
			sb.append(' ');
		}

		sb.append(line);
		writeQueue.add(sb.toString());
	}

	public synchronized void flush(boolean sync) {
		if (writeQueue.isEmpty()) {
			return;
		}

		var lines = Arrays.asList(writeQueue.toArray(UtilsJS.EMPTY_STRING_ARRAY));
		writeQueue.clear();

		if (sync) {
			try {
				Files.write(logFile, lines, StandardOpenOption.APPEND);
			} catch (Exception ex) {
				logger.error("Failed to write to the log file: " + ex);
			}
		} else {
			scriptType.executor.execute(() -> {
				try {
					Files.write(logFile, lines, StandardOpenOption.APPEND);
				} catch (Exception ex) {
					logger.error("Failed to write to the log file: " + ex);
				}
			});
		}
	}

	public void log(Object... message) {
		for (var s : message) {
			info(s);
		}
	}

	public ConsoleLine info(Object message) {
		return log(infoLogFunction, LogType.INFO, null, message);
	}

	public ConsoleLine infof(String message, Object... args) {
		return info(message.formatted(args));
	}

	public ConsoleLine warn(Object message) {
		return log(warnLogFunction, LogType.WARN, null, message);
	}

	public ConsoleLine warn(String message, Throwable error, @Nullable Pattern exitPattern) {
		if (shouldPrint()) {
			var l = log(errorLogFunction, LogType.WARN, error, message.isEmpty() ? error.getMessage() : (message + ": " + error.getMessage()));
			handleError(l, error, exitPattern, !capturingErrors);
			return l;
		}

		return null;
	}

	public ConsoleLine warn(String message, Throwable error) {
		return warn(message, error, null);
	}

	public ConsoleLine warnf(String message, Object... args) {
		return warn(message.formatted(args));
	}

	public ConsoleLine error(Object message) {
		return log(errorLogFunction, LogType.ERROR, null, message);
	}

	public ConsoleLine error(String message, Throwable error, @Nullable Pattern exitPattern) {
		if (shouldPrint()) {
			var l = log(errorLogFunction, LogType.ERROR, error, message.isEmpty() ? error.getMessage() : (message + ": " + error.getMessage()));
			handleError(l, error, exitPattern, true);
			return l;
		}

		return null;
	}

	public ConsoleLine error(String message, Throwable throwable) {
		return error(message, throwable, null);
	}

	public ConsoleLine errorf(String message, Object... args) {
		return error(message.formatted(args));
	}

	public boolean shouldPrintDebug() {
		return debugEnabled && shouldPrint();
	}

	public ConsoleLine debug(Object message) {
		if (shouldPrintDebug()) {
			return log(debugLogFunction, LogType.DEBUG, null, message);
		}

		return null;
	}

	public ConsoleLine debugf(String message, Object... args) {
		if (shouldPrintDebug()) {
			return debug(message.formatted(args));
		}

		return null;
	}

	public void group() {
		group += "  ";
	}

	public void groupEnd() {
		if (group.length() >= 2) {
			group = group.substring(0, group.length() - 2);
		}
	}

	public void trace() {
		var elements = Thread.currentThread().getStackTrace();
		info("=== Stack Trace ===");

		for (var element : elements) {
			info("=\t" + element);
		}
	}

	public int getScriptLine() {
		var linep = new int[]{0};
		Context.getSourcePositionFromStack(scriptType.manager.get().context, linep);
		return linep[0];
	}

	public void printClass(String className, boolean tree) {
		try {
			var c = Class.forName(className);
			var sc = c.getSuperclass();

			info("=== " + c.getName() + " ===");
			info("= Parent class =");
			info("> " + (sc == null ? "-" : sc.getName()));

			var vars = new HashMap<String, VarFunc>();
			var funcs = new HashMap<String, VarFunc>();

			for (var field : c.getDeclaredFields()) {
				if ((field.getModifiers() & Modifier.PUBLIC) == 0 || (field.getModifiers() & Modifier.TRANSIENT) == 0) {
					continue;
				}

				var f = new VarFunc(field.getName(), field.getType());
				f.flags |= 1;

				if ((field.getModifiers() & Modifier.FINAL) == 0) {
					f.flags |= 2;
				}

				vars.put(f.name, f);
			}

			for (var method : c.getDeclaredMethods()) {
				if ((method.getModifiers() & Modifier.PUBLIC) == 0) {
					continue;
				}

				var f = new VarFunc(method.getName(), method.getReturnType());

				for (var i = 0; i < method.getParameterCount(); i++) {
					f.params.add(method.getParameters()[i].getType());
				}

				if (f.name.length() >= 4 && f.name.startsWith("get") && Character.isUpperCase(f.name.charAt(3)) && f.params.size() == 0) {
					var n = Character.toLowerCase(f.name.charAt(3)) + f.name.substring(4);
					var f0 = vars.get(n);

					if (f0 == null) {
						vars.put(n, new VarFunc(n, f.type));
						continue;
					} else if (f0.type.equals(f.type)) {
						f0.flags |= 1;
						continue;
					}
				}

				funcs.put(f.name, f);
			}

			info("= Variables and Functions =");

			if (vars.isEmpty() && funcs.isEmpty()) {
				info("-");
			} else {
				vars.values().stream().sorted().forEach(f -> info("> " + ((f.flags & 2) == 0 ? "val" : "var") + " " + f.name + ": " + getSimpleName(f.type)));
				funcs.values().stream().sorted().forEach(f -> info("> function " + f.name + "(" + f.params.stream().map(this::getSimpleName).collect(Collectors.joining(", ")) + "): " + getSimpleName(f.type)));
			}

			if (tree && sc != null) {
				info("");
				printClass(sc.getName(), true);
			}
		} catch (Throwable ex) {
			error("= Error loading class '" + className + "' =", ex);
		}
	}

	public void printClass(String className) {
		printClass(className, false);
	}

	private String getSimpleName(Class<?> c) {
		if (c.isPrimitive()) {
			return c.getName();
		}

		var s = c.getName();
		var i = s.lastIndexOf('.');
		s = s.substring(i + 1);
		i = s.lastIndexOf('$');
		s = s.substring(i + 1);
		return s;
	}

	public void printObject(@Nullable Object o, boolean tree) {
		if (o == null) {
			info("=== null ===");
		} else {
			info("=== " + o.getClass().getName() + " ===");
			info("= toString() =");
			info("> " + o);
			info("= hashCode() =");
			info("> " + Integer.toHexString(o.hashCode()));
			info("");
			printClass(o.getClass().getName(), tree);
		}
	}

	public void printObject(@Nullable Object o) {
		printObject(o, false);
	}

	public void handleError(ConsoleLine line, Throwable error, @Nullable Pattern exitPattern, boolean print) {
		while (error instanceof WrappedException ex) {
			error = ex.getWrappedException();
		}

		if (error instanceof EcmaError) {
			return;
		}

		line.stackTrace = new ArrayList<>();
		error.printStackTrace(new StackTraceCollector(line.stackTrace, exitPattern, ERROR_REDUCE));

		if (print && !(error instanceof MutedError err && err.isMuted())) {
			for (var s : line.stackTrace) {
				line.type.callback.accept(logger, s);

				if (writeToFile) {
					writeToFile(line.type, line.timestamp, s);
				}
			}
		}
	}

	public Component errorsComponent(String command) {
		return Component.literal("KubeJS errors found [" + errors.size() + "]! Run '" + command + "' for more info")
			.kjs$clickRunCommand(command)
			.kjs$hover(Component.literal("Click to show"))
			.withStyle(ChatFormatting.DARK_RED);
	}

	private static final class VarFunc implements Comparable<VarFunc> {
		public final String name;
		public final Class<?> type;
		public final ArrayList<Class<?>> params;
		public int flags;

		public VarFunc(String n, Class<?> t) {
			name = n;
			type = t;
			flags = 0;
			params = new ArrayList<>();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}

			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			var varFunc = (VarFunc) o;
			return Objects.equals(name, varFunc.name) && Objects.equals(type, varFunc.type) && Objects.equals(flags, varFunc.flags) && Objects.equals(params, varFunc.params);
		}

		@Override
		public int hashCode() {
			return Objects.hash(name, type, flags, params);
		}

		@Override
		public int compareTo(VarFunc o) {
			return name.compareToIgnoreCase(o.name);
		}
	}
}
