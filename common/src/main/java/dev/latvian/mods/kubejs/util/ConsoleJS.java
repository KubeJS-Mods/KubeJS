package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.RhinoException;
import dev.latvian.mods.rhino.WrappedException;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author LatvianModder
 */
public class ConsoleJS {
	public static ConsoleJS STARTUP;
	public static ConsoleJS SERVER;
	public static ConsoleJS CLIENT;

	public static ConsoleJS getCurrent(ConsoleJS def) {
		Context cx = ScriptManager.getCurrentContext();

		if (cx != null && cx.sharedContextData.getExtraProperty("Console") instanceof ConsoleJS c) {
			return c;
		}

		return def;
	}

	private static class StackTracePrintStream extends PrintStream {
		private final ConsoleJS console;
		private boolean first;
		private final Pattern skipString;
		private boolean skip;

		private StackTracePrintStream(ConsoleJS c, @Nullable Pattern ca) {
			super(System.err);
			console = c;
			first = true;
			skipString = ca;
			skip = false;
		}

		@Override
		public void println(@Nullable Object x) {
			println(String.valueOf(x));
		}

		@Override
		public void println(@Nullable String x) {
			if (skip) {
				return;
			} else if (first && x != null) {
				console.scriptType.errors.add(x);
				first = false;
			}

			if (x != null && skipString != null && skipString.matcher(x).find()) {
				skip = true;
			} else {
				console.log(console.logger::error, "ERR  ", x);
			}
		}
	}

	private final ScriptType scriptType;
	private final Logger logger;
	private final Path logFile;
	private String group;
	private RhinoException currentError;
	private boolean muted;
	private boolean debugEnabled;
	private boolean writeToFile;
	private final List<String> writeQueue;
	private final String nameStrip;

	public ConsoleJS(ScriptType m, Logger log) {
		scriptType = m;
		logger = log;
		logFile = m.getLogFile();
		group = "";
		muted = false;
		debugEnabled = false;
		writeToFile = true;
		writeQueue = new ArrayList<>();
		nameStrip = scriptType.name + ':';
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

	public void setWriteToFile(boolean m) {
		writeToFile = m;
	}

	public boolean getWriteToFile() {
		return writeToFile;
	}

	public void resetFile() {
		scriptType.executor.execute(() -> {
			try {
				Files.write(logFile, Collections.emptyList());
			} catch (Exception ex) {
				logger.error("Failed to clear the log file: " + ex);
			}
		});
	}

	public void pushError(RhinoException e) {
		currentError = e;
	}

	public void popError() {
		currentError = null;
	}

	private String string(Object object) {
		var o = UtilsJS.wrap(object, JSObjectType.ANY);
		var s = o == null || o.getClass().isPrimitive() || o instanceof Boolean || o instanceof String || o instanceof Number || o instanceof WrappedJS ? String.valueOf(o) : (o + " [" + o.getClass().getName() + "]");

		var builder = new StringBuilder();

		int[] lineP = {0};
		String lineS = null;

		if (currentError != null) {
			lineP[0] = currentError.lineNumber();
			lineS = currentError.lineSource();
		}

		if (lineP[0] == 0 || lineS == null) {
			lineS = Context.getSourcePositionFromStack(scriptType.manager.get().context, lineP);
		}

		if (lineS != null && lineS.startsWith(nameStrip)) {
			lineS = lineS.substring(nameStrip.length());
		}

		if (lineP[0] > 0) {
			if (lineS != null && !lineS.isEmpty()) {
				builder.append(lineS);
				builder.append(':');
			} else {
				builder.append('#');
			}

			builder.append(lineP[0]);
			builder.append(": ");
		}

		builder.append(group);
		builder.append(s);
		return builder.toString();
	}

	private String stringf(Object object, Object... args) {
		return string(String.format(String.valueOf(object), args));
	}

	private void log(Consumer<String> logFunction, String type, Object message) {
		if (shouldPrint()) {
			var s = string(message);
			logFunction.accept(s);
			writeToFile(type, s);
		}
	}

	private void logf(Consumer<String> logFunction, String type, Object message, Object... args) {
		if (!shouldPrint()) {
			var s = stringf(message, args);
			logFunction.accept(s);
			writeToFile(type, s);
		}
	}

	private void writeToFile(String type, String line) {
		if (!writeToFile) {
			return;
		}

		var calendar = Calendar.getInstance();
		var sb = new StringBuilder();

		if (type.equals("ERR")) {
			sb.append('!');
			sb.append(' ');
		}

		sb.append('[');

		if (calendar.get(Calendar.HOUR_OF_DAY) < 10) {
			sb.append('0');
		}

		sb.append(calendar.get(Calendar.HOUR_OF_DAY));
		sb.append(':');

		if (calendar.get(Calendar.MINUTE) < 10) {
			sb.append('0');
		}

		sb.append(calendar.get(Calendar.MINUTE));
		sb.append(':');

		if (calendar.get(Calendar.SECOND) < 10) {
			sb.append('0');
		}

		sb.append(calendar.get(Calendar.SECOND));
		sb.append(']');
		sb.append(' ');
		sb.append('[');
		sb.append(type);
		sb.append(']');
		sb.append(' ');
		sb.append(line);
		writeQueue.add(sb.toString());
	}

	public synchronized void flush(boolean sync) {
		if (writeQueue.isEmpty()) {
			return;
		}

		List<String> lines = new ArrayList<>(writeQueue);
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

	public void info(Object message) {
		log(logger::info, "INFO ", message);
	}

	public void infof(Object message, Object... args) {
		logf(logger::info, "INFO ", message, args);
	}

	public void log(Object message) {
		info(message);
	}

	public void warn(Object message) {
		log(s -> {
			logger.warn(s);
			scriptType.warnings.add(s);
		}, "WARN ", message);
	}

	public void warn(String message, Throwable throwable, @Nullable Pattern skip) {
		if (shouldPrint()) {
			var s = throwable.toString();

			if (CommonProperties.get().debugInfo || s.equals("java.lang.NullPointerException")) {
				warn(message + ":");
				printStackTrace(throwable, skip);
			} else {
				warn(message + ": " + s);
			}
		}
	}

	public void warn(String message, Throwable throwable) {
		warn(message, throwable, null);
	}

	public void warnf(String message, Object... args) {
		logf(this::warn0, "WARN ", message, args);
	}

	private void warn0(String s) {
		logger.warn(s);
		scriptType.warnings.add(s);
	}

	public void error(Object message) {
		log(this::error0, "ERR", message);
	}

	private void error0(String s) {
		logger.error("! " + s);
		scriptType.errors.add(s);
	}

	public void error(String message, Throwable throwable, @Nullable Pattern skip) {
		if (shouldPrint()) {
			var s = throwable.toString();

			if (CommonProperties.get().debugInfo || s.equals("java.lang.NullPointerException")) {
				error(message + ":");
				printStackTrace(throwable, skip);
			} else {
				error(message + ": " + s);
			}
		}
	}

	public void error(String message, Throwable throwable) {
		error(message, throwable, null);
	}

	public void errorf(String message, Object... args) {
		logf(s -> {
			logger.error(s);
			scriptType.errors.add(s);
		}, "ERR ", message, args);
	}

	public boolean shouldPrintDebug() {
		return debugEnabled && shouldPrint();
	}

	public void debug(Object message) {
		if (shouldPrintDebug()) {
			log(logger::debug, "DEBUG", message);
		}
	}

	public void debugf(String message, Object... args) {
		if (shouldPrintDebug()) {
			logf(logger::debug, "DEBUG", message, args);
		}
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
				if ((method.getModifiers() & Modifier.PUBLIC) == 0 || isOverrideMethod(method)) {
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
			error("= Error loading class =");
			error(ex.toString());
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

	private boolean isOverrideMethod(Method method) throws Throwable {
		return false;
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

	public void printStackTrace(Throwable throwable, @Nullable Pattern skip) {
		throwable.printStackTrace(new StackTracePrintStream(this, skip));
	}

	public void handleError(Throwable throwable, @Nullable Pattern skip, String message) {
		try {
			if (throwable instanceof WrappedException ex) {
				pushError(ex);
				error(message + ": " + ex.getWrappedException());
				popError();
				printStackTrace(ex.getWrappedException(), skip);
			} else if (throwable instanceof RhinoException ex) {
				pushError(ex);
				error(message + ": " + ex.getMessage());
				popError();
			} else {
				error(message + ": " + throwable);
				printStackTrace(throwable, skip);
			}
		} catch (Throwable ex) {
			error("Errored while handling error... wtf... " + ex);
			ex.printStackTrace();
		}
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
			return Objects.equals(name, varFunc.name) &&
						   Objects.equals(type, varFunc.type) &&
						   Objects.equals(flags, varFunc.flags) &&
						   Objects.equals(params, varFunc.params);
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
