package dev.latvian.mods.kubejs.util;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.List;

public record TickDuration(long ticks) implements TemporalAmount {
	public static final TickDuration ZERO = new TickDuration(0L);
	private static final List<TemporalUnit> UNITS = List.of(TickTemporalUnit.INSTANCE);

	@Override
	public long get(TemporalUnit unit) {
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
