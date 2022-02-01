package dev.latvian.kubejs.util;

import dev.latvian.kubejs.CommonProperties;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.Context;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.lang.reflect.Field;
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
				console.type.errors.add(x);
				first = false;
			}

			if (x != null && skipString != null && skipString.matcher(x).find()) {
				skip = true;
			} else {
				console.log(console.logger::error, "ERR  ", x);
			}
		}
	}

	private final ScriptType type;
	private final Logger logger;
	private final Path logFile;
	private String group;
	private int lineNumber;
	private boolean muted;
	private boolean debugEnabled;
	private boolean writeToFile;
	private final List<String> writeQueue;

	public ConsoleJS(ScriptType m, Logger log) {
		type = m;
		logger = log;
		logFile = m.getLogFile();
		group = "";
		lineNumber = 0;
		muted = false;
		debugEnabled = false;
		writeToFile = true;
		writeQueue = new ArrayList<>();
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
		type.executor.execute(() -> {
			try {
				Files.write(logFile, Collections.emptyList());
			} catch (Exception ex) {
				logger.error("Failed to clear the log file: " + ex);
			}
		});
	}

	public void setLineNumber(boolean b) {
		lineNumber += b ? 1 : -1;
	}

	private String string(Object object) {
		Object o = UtilsJS.wrap(object, JSObjectType.ANY);
		String s = o == null || o.getClass().isPrimitive() || o instanceof Boolean || o instanceof String || o instanceof Number || o instanceof WrappedJS ? String.valueOf(o) : (o + " [" + o.getClass().getName() + "]");

		if (lineNumber <= 0 && group.isEmpty()) {
			return s;
		}

		StringBuilder builder = new StringBuilder();

		if (lineNumber > 0) {
			int[] lineP = {0};
			String lineS = Context.getSourcePositionFromStack(lineP);

			if (lineP[0] > 0) {
				if (lineS != null) {
					builder.append(lineS);
				}

				builder.append(':');
				builder.append(lineP[0]);
				builder.append(": ");
			}
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
			String s = string(message);
			logFunction.accept(s);
			writeToFile(type, s);
		}
	}

	private void logf(Consumer<String> logFunction, String type, Object message, Object... args) {
		if (!shouldPrint()) {
			String s = stringf(message, args);
			logFunction.accept(s);
			writeToFile(type, s);
		}
	}

	private void writeToFile(String type, String line) {
		if (!writeToFile) {
			return;
		}

		Calendar calendar = Calendar.getInstance();
		StringBuilder sb = new StringBuilder();
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

	public synchronized void flush() {
		if (writeQueue.isEmpty()) {
			return;
		}

		List<String> lines = new ArrayList<>(writeQueue);
		writeQueue.clear();

		type.executor.execute(() -> {
			try {
				Files.write(logFile, lines, StandardOpenOption.APPEND);
			} catch (Exception ex) {
				logger.error("Failed to write to the log file: " + ex);
			}
		});
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
			type.warnings.add(s);
		}, "WARN ", message);
	}

	public void warn(String message, Throwable throwable, @Nullable Pattern skip) {
		if (shouldPrint()) {
			String s = throwable.toString();

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
		logf(s -> {
			logger.warn(s);
			type.warnings.add(s);
		}, "WARN ", message, args);
	}

	public void error(Object message) {
		log(s -> {
			logger.error(s);
			type.errors.add(s);
		}, "ERR  ", message);
	}

	public void error(String message, Throwable throwable, @Nullable Pattern skip) {
		if (shouldPrint()) {
			String s = throwable.toString();

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
			type.errors.add(s);
		}, "ERR  ", message, args);
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
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		info("=== Stack Trace ===");

		for (StackTraceElement element : elements) {
			info("=\t" + element);
		}
	}

	public int getScriptLine() {
		int[] linep = {0};
		Context.getSourcePositionFromStack(linep);
		return linep[0];
	}

	public void printClass(String className, boolean tree) {
		setLineNumber(true);

		try {
			Class<?> c = Class.forName(className);
			Class<?> sc = c.getSuperclass();

			info("=== " + c.getName() + " ===");
			info("= Parent class =");
			info("> " + (sc == null ? "-" : sc.getName()));

			HashMap<String, VarFunc> vars = new HashMap<>();
			HashMap<String, VarFunc> funcs = new HashMap<>();

			for (Field field : c.getDeclaredFields()) {
				if ((field.getModifiers() & Modifier.PUBLIC) == 0 || (field.getModifiers() & Modifier.TRANSIENT) == 0) {
					continue;
				}

				VarFunc f = new VarFunc(field.getName(), field.getType());
				f.flags |= 1;

				if ((field.getModifiers() & Modifier.FINAL) == 0) {
					f.flags |= 2;
				}

				vars.put(f.name, f);
			}

			for (Method method : c.getDeclaredMethods()) {
				if ((method.getModifiers() & Modifier.PUBLIC) == 0 || isOverrideMethod(method)) {
					continue;
				}

				VarFunc f = new VarFunc(method.getName(), method.getReturnType());

				for (int i = 0; i < method.getParameterCount(); i++) {
					f.params.add(method.getParameters()[i].getType());
				}

				if (f.name.length() >= 4 && f.name.startsWith("get") && Character.isUpperCase(f.name.charAt(3)) && f.params.size() == 0) {
					String n = Character.toLowerCase(f.name.charAt(3)) + f.name.substring(4);
					VarFunc f0 = vars.get(n);

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

		setLineNumber(false);
	}

	public void printClass(String className) {
		printClass(className, false);
	}

	private String getSimpleName(Class<?> c) {
		if (c.isPrimitive()) {
			return c.getName();
		}

		String s = c.getName();
		int i = s.lastIndexOf('.');
		s = s.substring(i + 1);
		i = s.lastIndexOf('$');
		s = s.substring(i + 1);
		return s;
	}

	private boolean isOverrideMethod(Method method) throws Throwable {
		return false;
	}

	public void printObject(@Nullable Object o, boolean tree) {
		setLineNumber(true);

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

		setLineNumber(false);
	}

	public void printObject(@Nullable Object o) {
		printObject(o, false);
	}

	public void printStackTrace(Throwable throwable, @Nullable Pattern skip) {
		throwable.printStackTrace(new StackTracePrintStream(this, skip));
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

			VarFunc varFunc = (VarFunc) o;
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
