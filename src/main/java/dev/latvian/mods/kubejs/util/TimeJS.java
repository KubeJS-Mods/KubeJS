package dev.latvian.mods.kubejs.util;

import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.Calendar;
import java.util.regex.Pattern;

public interface TimeJS {
	Pattern TEMPORAL_AMOUNT_PATTERN = Pattern.compile("(\\d+)\\s*(y|M|d|w|h|m|s|ms|ns|t)\\b");

	static TemporalAmount wrapTemporalAmount(Object o) {
		return switch (o) {
			case TemporalAmount d -> d;
			case Number n -> Duration.ofMillis(n.longValue());
			case CharSequence cs -> {
				var matcher = TEMPORAL_AMOUNT_PATTERN.matcher(cs.toString());

				var millis = 0D;
				var nanos = 0D;
				var ticks = Double.NaN;

				while (matcher.find()) {
					var amount = Double.parseDouble(matcher.group(1));

					switch (matcher.group(2)) {
						case "t" -> {
							if (Double.isNaN(ticks)) {
								ticks = 0D;
							}

							ticks += amount;
						}

						case "ns" -> nanos += amount;
						case "ms" -> millis += amount;
						case "s" -> millis = amount * 1000D;
						case "m" -> millis = amount * 60000D;
						case "h" -> millis = amount * 60000D * 60L;
						case "d" -> millis = amount * 86400L * 1000L;
						case "w" -> millis = amount * 86400L * 7000L;
						case "M" -> millis = amount * 31556952D / 12D * 1000L;
						case "y" -> millis = amount * 31556952D * 1000L;
						default -> throw new IllegalArgumentException("Invalid temporal unit: " + matcher.group(2));
					}
				}

				if (!Double.isNaN(ticks)) {
					yield TickDuration.of((long) (ticks + millis / 50D));
				}

				yield Duration.ofMillis((long) millis).plusNanos((long) nanos);
			}
			case null, default -> throw new IllegalArgumentException("Invalid temporal amount: " + o);
		};
	}

	static Duration wrapDuration(Object o) {
		var t = wrapTemporalAmount(o);

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
