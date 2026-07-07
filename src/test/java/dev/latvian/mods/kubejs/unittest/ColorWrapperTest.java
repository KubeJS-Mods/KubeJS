package dev.latvian.mods.kubejs.unittest;

import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ColorWrapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ColorWrapperTest {
	@Test
	void nullAndBlankMapToNone() {
		assertThat(ColorWrapper.wrap(null)).isSameAs(ColorWrapper.NONE);
		assertThat(ColorWrapper.wrap("")).isSameAs(ColorWrapper.NONE);
		assertThat(ColorWrapper.wrap("transparent")).isSameAs(ColorWrapper.NONE);
	}

	@Test
	void unknownStringMapsToNone() {
		assertThat(ColorWrapper.wrap("this_is_not_a_color")).isSameAs(ColorWrapper.NONE);
	}

	@Test
	void hexStringMakesAColor() {
		assertThat(ColorWrapper.wrap("#abcdef")).isNotSameAs(ColorWrapper.NONE);
		assertThat(ColorWrapper.wrap("#80abcdef")).isNotSameAs(ColorWrapper.NONE);
	}

	@Test
	void nonZeroNumberMakesAColor() {
		assertThat(ColorWrapper.wrap(0x123456)).isNotSameAs(ColorWrapper.NONE);
		assertThat(ColorWrapper.wrap(0)).isSameAs(ColorWrapper.NONE);
	}

	@Test
	void rgbaBuildsColor() {
		assertThat(ColorWrapper.rgba(255, 0, 0, 255)).isNotNull();
	}
}
