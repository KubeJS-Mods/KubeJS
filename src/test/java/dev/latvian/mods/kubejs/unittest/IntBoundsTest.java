package dev.latvian.mods.kubejs.unittest;

import dev.latvian.mods.kubejs.util.IntBounds;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IntBoundsTest {
	@Test
	void commonBoundsAreInterned() {
		assertThat(IntBounds.of(1, Integer.MAX_VALUE)).isSameAs(IntBounds.DEFAULT);
		assertThat(IntBounds.of(0, Integer.MAX_VALUE)).isSameAs(IntBounds.OPTIONAL);
	}

	@Test
	void otherBoundsAreDistinct() {
		var bounds = IntBounds.of(2, 5);
		assertThat(bounds).isNotSameAs(IntBounds.DEFAULT).isNotSameAs(IntBounds.OPTIONAL);
		assertThat(bounds.min()).isEqualTo(2);
		assertThat(bounds.max()).isEqualTo(5);
	}

	@Test
	void internedBoundsExposeExpectedValues() {
		assertThat(IntBounds.DEFAULT.min()).isEqualTo(1);
		assertThat(IntBounds.OPTIONAL.min()).isEqualTo(0);
		assertThat(IntBounds.DEFAULT.max()).isEqualTo(Integer.MAX_VALUE);
	}
}
