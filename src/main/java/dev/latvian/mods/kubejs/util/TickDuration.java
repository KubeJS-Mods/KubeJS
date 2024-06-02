package dev.latvian.mods.kubejs.util;

import com.mojang.serialization.Codec;
import dev.latvian.mods.rhino.type.TypeInfo;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.List;

public record TickDuration(long ticks) implements TemporalAmount {
	public static final TickDuration ZERO = new TickDuration(0L);
	private static final List<TemporalUnit> UNITS = List.of(TickTemporalUnit.INSTANCE);
	public static final Codec<TickDuration> CODEC = Codec.LONG.xmap(TickDuration::new, TickDuration::ticks);
	public static final Codec<TickDuration> SECONDS_CODEC = Codec.DOUBLE.xmap(l -> new TickDuration((long) (l * 20D)), t -> t.ticks() / 20D);
	public static final Codec<TickDuration> MINUTES_CODEC = Codec.DOUBLE.xmap(l -> new TickDuration((long) (l * 1200L)), t -> t.ticks() / 1200D);
	public static final Codec<TickDuration> HOURS_CODEC = Codec.DOUBLE.xmap(l -> new TickDuration((long) (l * 72000L)), t -> t.ticks() / 72000D);

	public static final TypeInfo TYPE_INFO = TypeInfo.of(TickDuration.class); // TypeInfo.NUMBER.or(TypeInfo.STRING)

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
		if (ticks != 0) {
			return temporal.plus(ticks, TickTemporalUnit.INSTANCE);
		}

		return temporal;
	}

	@Override
	public Temporal subtractFrom(Temporal temporal) {
		if (ticks != 0) {
			return temporal.minus(ticks, TickTemporalUnit.INSTANCE);
		}

		return temporal;
	}
}
