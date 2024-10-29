package dev.latvian.mods.kubejs.util;

import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.function.Function;
import java.util.regex.Pattern;

public class StackTraceCollector extends PrintStream {
	private static final OutputStream OUTPUT_SINK = new OutputStream() {
		@Override
		public void write(int b) {
		}

		@Override
		public void write(byte[] b) {
		}

		@Override
		public void write(byte[] b, int off, int len) {
		}
	};

	private final Collection<String> stackTrace;
	private final Pattern exitPattern;
	private final Function<String, String> reduce;
	private boolean exit;

	public StackTraceCollector(Collection<String> stackTrace, @Nullable Pattern exitPattern, Function<String, String> reduce) {
		super(OUTPUT_SINK);
		this.stackTrace = stackTrace;
		this.exitPattern = exitPattern;
		this.reduce = reduce;
		this.exit = false;
	}

	@Override
	public void print(@Nullable String s) {
		if (s != null && !s.isEmpty()) {
			for (var str : s.split("\n")) {
				println(str);
			}
		}
	}

	@Override
	public void println(@Nullable Object x) {
		println(String.valueOf(x));
	}

	@Override
	public void println(@Nullable String x) {
		if (exit || x == null || x.isEmpty()) {
			return;
		}

		boolean isAt = x.startsWith("\tat ");

		if (isAt) {
			x = x.substring(4);
		}

		x = x.trim();

		if (x.startsWith("java.base/")) {
			x = x.substring(10);
		}

		if (exitPattern != null && exitPattern.matcher(x).find()) {
			exit = true;
			return;
		}

		x = reduce.apply(x);

		if (x == null || x.isEmpty()) {
			return;
		}

		if (isAt) {
			stackTrace.add("  at " + x);
		} else {
			stackTrace.add(x);
		}
	}
}
