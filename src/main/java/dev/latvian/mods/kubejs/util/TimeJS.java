package dev.latvian.mods.kubejs.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.Undefined;

import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.Calendar;

import static com.mojang.serialization.DataResult.error;

public interface TimeJS {
	static TemporalAmount wrapTemporalAmount(Context cx, Object o) {
		return switch (o) {
			case TemporalAmount d -> d;
			case Number n -> Duration.ofMillis(n.longValue());
			case null -> throw new KubeRuntimeException("Cannot convert null to temporal amount!").source(SourceLine.of(cx));
			case Undefined undefined -> throw new KubeRuntimeException("Cannot convert undefined to temporal amount!").source(SourceLine.of(cx));
			case Scriptable s when Undefined.isUndefined(s) -> throw new KubeRuntimeException("Cannot convert undefined to temporal amount!").source(SourceLine.of(cx));
			case CharSequence cs -> {
				try {
					yield readTemporalAmount(new StringReader(cs.toString()));
				} catch (CommandSyntaxException ex) {
					throw new KubeRuntimeException("Failed to parse temporal amount: %s".formatted(cs), ex).source(SourceLine.of(cx));
				}
			}
			default -> throw new KubeRuntimeException("Don't know how to parse temporal amount from %s".formatted(o)).source(SourceLine.of(cx));
		};
	}

	private static TemporalAmount readTemporalAmount(StringReader reader) throws CommandSyntaxException {
		reader.skipWhitespace();

		if (!reader.canRead()) {
			throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedDouble().createWithContext(reader);
		}

		var totalNanos = 0D;
		var ticks = Double.NaN;

		while (reader.canRead()) {
			var amount = reader.readDouble();
			reader.skipWhitespace();

			switch (readTemporalUnit(reader)) {
				case "t" -> {
					if (Double.isNaN(ticks)) {
						ticks = 0D;
					}

					ticks += amount;
				}
				case "ns" -> totalNanos += amount;
				case "ms" -> totalNanos += amount * 1_000_000D;
				case "s" -> totalNanos += amount * 1_000_000_000D;
				case "m" -> totalNanos += amount * 60D * 1_000_000_000D;
				case "h" -> totalNanos += amount * 3600D * 1_000_000_000D;
				case "d" -> totalNanos += amount * 86400D * 1_000_000_000D;
				case "w" -> totalNanos += amount * 604800D * 1_000_000_000D;
				case "M" -> totalNanos += amount * (31556952D / 12D) * 1_000_000_000D;
				case "y" -> totalNanos += amount * 31556952D * 1_000_000_000D;
				default -> throw new IllegalStateException("Unexpected temporal unit!");
			}

			reader.skipWhitespace();
		}

		if (!Double.isNaN(ticks)) {
			return TickDuration.of((long) (ticks + totalNanos / 50_000_000D));
		}

		return Duration.ofNanos((long) totalNanos);
	}

	private static String readTemporalUnit(StringReader reader) throws CommandSyntaxException {
		if (!reader.canRead()) {
			throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedSymbol().createWithContext(reader, "<time unit>");
		}

		if (reader.canRead(2)) {
			if (reader.peek() == 'm' && reader.peek(1) == 's') {
				reader.skip();
				reader.skip();
				return "ms";
			} else if (reader.peek() == 'n' && reader.peek(1) == 's') {
				reader.skip();
				reader.skip();
				return "ns";
			}
		}

		return switch (reader.read()) {
			case 'y' -> "y";
			case 'M' -> "M";
			case 'd' -> "d";
			case 'w' -> "w";
			case 'h' -> "h";
			case 'm' -> "m";
			case 's' -> "s";
			case 't' -> "t";
			default -> throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedSymbol().createWithContext(reader, "<time unit>");
		};
	}

	static Duration wrapDuration(Context cx, Object o) {
		var t = wrapTemporalAmount(cx, o);

		return switch (t) {
			case Duration d -> d;
			case TickDuration(long ticks) -> Duration.ofMillis(ticks * 50L);
			default -> {
				var d = Duration.ZERO;

				for (var unit : t.getUnits()) {
					d = d.plus(t.get(unit), unit);
				}

				yield d;
			}
		};
	}

	static DataResult<Duration> readDuration(String s) {
		try {
			var reader = new StringReader(s);
			reader.skipWhitespace();

			var temporalAmount = readTemporalAmount(reader);

			return DataResult.success(switch (temporalAmount) {
				case Duration d -> d;
				case TickDuration(long ticks) -> Duration.ofMillis(ticks * 50L);
				default -> {
					var d = Duration.ZERO;

					for (var unit : temporalAmount.getUnits()) {
						d = d.plus(temporalAmount.get(unit), unit);
					}

					yield d;
				}
			});
		} catch (CommandSyntaxException ex) {
			return error(() -> "Error parsing %s from string: %s".formatted(s, ex));
		}
	}

	static void appendTimestamp(StringBuilder builder, Calendar calendar) {
		int h = calendar.get(Calendar.HOUR_OF_DAY);
		int m = calendar.get(Calendar.MINUTE);
		int s = calendar.get(Calendar.SECOND);

		if (h < 10) {
			builder.append('0');
		}

		builder.append(h);
		builder.append(':');

		if (m < 10) {
			builder.append('0');
		}

		builder.append(m);
		builder.append(':');

		if (s < 10) {
			builder.append('0');
		}

		builder.append(s);
	}

	static String msToString(long ms) {
		if (ms < 1000L) {
			return ms + " ms";
		} else {
			return "%.3f".formatted(ms / 1000F) + " s";
		}
	}
}
