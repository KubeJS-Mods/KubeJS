package dev.latvian.mods.kubejs.util;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.KubeJSCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.Calendar;
import java.util.regex.Pattern;

public interface TimeJS {
	Pattern TEMPORAL_AMOUNT_PATTERN = Pattern.compile("(\\d+)\\s*(y|M|d|w|h|m|s|ms|ns|t)\\b");
	Codec<Duration> DURATION = KubeJSCodecs.stringResolverCodec(Duration::toString, TimeJS::durationOf);
	StreamCodec<ByteBuf, Duration> DURATION_STREAM = ByteBufCodecs.STRING_UTF8.map(TimeJS::durationOf, Duration::toString);

	static TemporalAmount temporalAmountOf(Object o) {
		if (o instanceof TemporalAmount d) {
			return d;
		} else if (o instanceof Number n) {
			return Duration.ofMillis(n.longValue());
		} else if (o instanceof CharSequence) {
			var matcher = TEMPORAL_AMOUNT_PATTERN.matcher(o.toString());

			var millis = 0L;
			var nanos = 0L;
			var ticks = -1L;

			while (matcher.find()) {
				var amount = Double.parseDouble(matcher.group(1));

				switch (matcher.group(2)) {
					case "t" -> {
						if (ticks == -1L) {
							ticks = 0L;
						}

						ticks += amount;
					}

					case "ns" -> nanos += (long) amount;
					case "ms" -> millis += (long) amount;
					case "s" -> millis = (long) (amount * 1000D);
					case "m" -> millis = (long) (amount * 60000D);
					case "h" -> millis = (long) (amount * 60000D) * 60L;
					case "d" -> millis = (long) (amount * 24D * 86400L) * 1000L;
					case "w" -> millis = (long) (amount * 24D * 86400L) * 7000L;
					case "M" -> millis = (long) (amount * 31556952D / 12D) * 1000L;
					case "y" -> millis = (long) (amount * 31556952D) * 1000L;
					default -> throw new IllegalArgumentException("Invalid temporal unit: " + matcher.group(2));
				}
			}

			if (ticks != -1L) {
				return new TickDuration(ticks + millis / 50L);
			}

			return Duration.ofMillis(millis).plusNanos(nanos);
		} else {
			throw new IllegalArgumentException("Invalid temporal amount: " + o);
		}
	}

	static long tickDurationOf(Object o) {
		if (o instanceof Number n) {
			return n.longValue();
		} else if (o instanceof JsonPrimitive json) {
			return json.getAsLong();
		}

		var t = temporalAmountOf(o);

		if (t instanceof TickDuration d) {
			return d.ticks();
		} else if (t instanceof Duration d) {
			return d.toMillis() / 50L;
		} else {
			return 0L;
		}
	}

	static Duration durationOf(Object o) {
		var t = temporalAmountOf(o);

		if (t instanceof Duration d) {
			return d;
		} else if (t instanceof TickDuration d) {
			return Duration.ofMillis(d.ticks() * 50L);
		} else {
			var d = Duration.ZERO;

			for (var unit : t.getUnits()) {
				d = d.plus(t.get(unit), unit);
			}

			return d;
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
