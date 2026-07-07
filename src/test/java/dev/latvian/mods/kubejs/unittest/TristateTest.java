package dev.latvian.mods.kubejs.unittest;

import dev.latvian.mods.kubejs.util.Tristate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TristateTest {
	@Test
	void wrapCoercesCommonValues() {
		assertThat(Tristate.wrap(null)).isEqualTo(Tristate.DEFAULT);
		assertThat(Tristate.wrap(true)).isEqualTo(Tristate.TRUE);
		assertThat(Tristate.wrap(false)).isEqualTo(Tristate.FALSE);
		assertThat(Tristate.wrap(Tristate.TRUE)).isEqualTo(Tristate.TRUE);
	}

	@Test
	void wrapParsesStringsCaseInsensitively() {
		assertThat(Tristate.wrap("TRUE")).isEqualTo(Tristate.TRUE);
		assertThat(Tristate.wrap("false")).isEqualTo(Tristate.FALSE);
		assertThat(Tristate.wrap("garbage")).isEqualTo(Tristate.DEFAULT);
	}

	@Test
	void defaultAlwaysPasses() {
		assertThat(Tristate.DEFAULT.test(true)).isTrue();
		assertThat(Tristate.DEFAULT.test(false)).isTrue();
	}

	@Test
	void trueAndFalseMatchTheirState() {
		assertThat(Tristate.TRUE.test(true)).isTrue();
		assertThat(Tristate.TRUE.test(false)).isFalse();
		assertThat(Tristate.FALSE.test(false)).isTrue();
		assertThat(Tristate.FALSE.test(true)).isFalse();
	}

	@Test
	void serializedNameMatchesToken() {
		assertThat(Tristate.DEFAULT.getSerializedName()).isEqualTo("default");
	}
}
