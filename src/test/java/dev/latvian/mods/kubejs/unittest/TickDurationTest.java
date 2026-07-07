package dev.latvian.mods.kubejs.unittest;

import dev.latvian.mods.kubejs.util.TickDuration;
import dev.latvian.mods.kubejs.util.TickTemporalUnit;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class TickDurationTest {
	@Test
	void zeroIsInterned() {
		assertThat(TickDuration.of(0L)).isSameAs(TickDuration.ZERO);
		assertThat(TickDuration.of(5L).ticks()).isEqualTo(5L);
	}

	@Test
	void intTicksClampsToIntRange() {
		assertThat(TickDuration.of(Long.MAX_VALUE).intTicks()).isEqualTo(Integer.MAX_VALUE);
		assertThat(TickDuration.of(7L).intTicks()).isEqualTo(7);
	}

	@Test
	void getReturnsTicksOnlyForTickUnit() {
		var d = TickDuration.of(5L);
		assertThat(d.get(TickTemporalUnit.INSTANCE)).isEqualTo(5L);
		assertThat(d.get(ChronoUnit.SECONDS)).isEqualTo(0L);
	}

	@Test
	void tickUnitIsFiftyMillis() {
		assertThat(TickTemporalUnit.INSTANCE.getDuration()).isEqualTo(Duration.ofMillis(50L));
		assertThat(TickTemporalUnit.INSTANCE.isTimeBased()).isTrue();
		assertThat(TickTemporalUnit.INSTANCE.isDateBased()).isFalse();
	}

	@Test
	void betweenDividesMillisByFifty() {
		assertThat(TickTemporalUnit.INSTANCE.between(Instant.EPOCH, Instant.ofEpochMilli(1000L))).isEqualTo(20L);
	}
}
