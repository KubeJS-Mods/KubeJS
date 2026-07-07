package dev.latvian.mods.kubejs.unittest;

import dev.latvian.mods.kubejs.block.MapColorHelper;
import net.minecraft.world.level.material.MapColor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MapColorHelperTest {
	@Test
	void reverseOfNoneIsNone() {
		assertThat(MapColorHelper.reverse(MapColor.NONE)).isSameAs(MapColorHelper.NONE);
	}

	@Test
	void findClosestAlwaysResolves() {
		assertThat(MapColorHelper.findClosest(0xFFFFFF)).isNotNull();
		assertThat(MapColorHelper.findClosest(0x000000)).isNotNull();
	}

	@Test
	void wrapResolvesNamesAndNumbers() {
		assertThat(MapColorHelper.wrap("none")).isNotNull();
		assertThat(MapColorHelper.wrap(0xFF0000)).isNotNull();
		assertThat(MapColorHelper.wrap(MapColor.NONE)).isSameAs(MapColor.NONE);
	}
}
