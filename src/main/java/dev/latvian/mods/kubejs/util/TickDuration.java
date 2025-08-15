package dev.latvian.mods.kubejs.util;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import dev.latvian.mods.rhino.type.TypeInfo;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.List;

public record TickDuration(long ticks) implements TemporalAmount {
	public static final TickDuration ZERO = new TickDuration(0L);
	private static final List<TemporalUnit> UNITS = List.of(TickTemporalUnit.INSTANCE);
	public static final Codec<TickDuration> CODEC = Codec.LONG.xmap(TickDuration::of, TickDuration::ticks);
	public static final Codec<TickDuration> SECONDS_CODEC = Codec.DOUBLE.xmap(l -> TickDuration.of((long) (l * 20D)), t -> t.ticks() / 20D);
	public static final Codec<TickDuration> MINUTES_CODEC = Codec.DOUBLE.xmap(l -> TickDuration.of((long) (l * 1200D)), t -> t.ticks() / 1200D);
	public static final Codec<TickDuration> HOURS_CODEC = Codec.DOUBLE.xmap(l -> TickDuration.of((long) (l * 72000D)), t -> t.ticks() / 72000D);

	public static final TypeInfo TYPE_INFO = TypeInfo.of(TickDuration.class); // TypeInfo.NUMBER.or(TypeInfo.STRING)

	public static TickDuration of(long ticks) {
		return ticks == 0L ? ZERO : new TickDuration(ticks);
	}

	public static TickDuration wrap(Object from) {
		return switch (from) {
			case null -> ZERO;
			case TickDuration d -> d;
			case Number n -> of(n.longValue());
			case JsonPrimitive json -> of(json.getAsLong());
			default -> of(TimeJS.wrapDuration(from).toMillis() / 50L);
		};
	}

	@Override
	public long get(TemporalUnit unit) {
		if (unit == TickTemporalUnit.INSTANCE) {
			return ticks;
		}

		return 0;
	}

	@Override
	public List<TemporalUnit> getUnits() {
		return UNITS;
	}

	@Override
	public Temporal addTo(Temporal temporal) {
		if (ticks != 0L) {
			return temporal.plus(ticks, TickTemporalUnit.INSTANCE);
		}

		return temporal;
	}

	@Override
	public Temporal subtractFrom(Temporal temporal) {
		if (ticks != 0L) {
			return temporal.minus(ticks, TickTemporalUnit.INSTANCE);
		}

		return temporal;
	}

	@Override
	public String toString() {
		return ticks + " ticks";
	}

	public int intTicks() {
		return Math.clamp(ticks, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}
}
