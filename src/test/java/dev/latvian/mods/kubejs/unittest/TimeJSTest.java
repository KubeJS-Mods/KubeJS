package dev.latvian.mods.kubejs.unittest;

import dev.latvian.mods.kubejs.util.TimeJS;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class TimeJSTest {
	@Test
	void readsSimpleUnits() {
		assertThat(TimeJS.readDuration("1s").result()).contains(Duration.ofSeconds(1));
		assertThat(TimeJS.readDuration("500ms").result()).contains(Duration.ofMillis(500));
	}

	@Test
	void readsTicksAsFiftyMillisEach() {
		assertThat(TimeJS.readDuration("20t").result()).contains(Duration.ofSeconds(1));
	}

	@Test
	void readsCompoundDuration() {
		assertThat(TimeJS.readDuration("1h30m").result()).contains(Duration.ofMinutes(90));
	}

	@Test
	void rejectsGarbage() {
		assertThat(TimeJS.readDuration("garbage").result()).isEmpty();
	}

	@Test
	void msToStringSwitchesUnitAtOneSecond() {
		assertThat(TimeJS.msToString(500L)).isEqualTo("500 ms");
		assertThat(TimeJS.msToString(1500L)).isEqualTo("1.500 s");
	}
}
