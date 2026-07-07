package dev.latvian.mods.kubejs.unittest;

import dev.latvian.mods.kubejs.plugin.builtin.wrapper.MiscWrappers;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MiscWrappersTest {
	@Test
	void wrapIntProviderFromNumber() {
		var provider = MiscWrappers.wrapIntProvider(null, 5);
		assertThat(provider).isInstanceOf(ConstantInt.class);
		assertThat(provider.minInclusive()).isEqualTo(5);
		assertThat(provider.maxInclusive()).isEqualTo(5);
	}

	@Test
	void wrapIntProviderFromSingletonList() {
		var provider = MiscWrappers.wrapIntProvider(null, List.of(4));
		assertThat(provider).isInstanceOf(ConstantInt.class);
		assertThat(provider.minInclusive()).isEqualTo(4);
		assertThat(provider.maxInclusive()).isEqualTo(4);
	}

	@Test
	void wrapIntProviderFromRangeList() {
		var provider = MiscWrappers.wrapIntProvider(null, List.of(3, 7));
		assertThat(provider).isInstanceOf(UniformInt.class);
		assertThat(provider.minInclusive()).isEqualTo(3);
		assertThat(provider.maxInclusive()).isEqualTo(7);
	}

	@Test
	void wrapIntProviderSortsRangeBounds() {
		var provider = MiscWrappers.wrapIntProvider(null, List.of(9, 2));
		assertThat(provider.minInclusive()).isEqualTo(2);
		assertThat(provider.maxInclusive()).isEqualTo(9);
	}

	@Test
	void wrapFloatProviderFromNumber() {
		var provider = MiscWrappers.wrapFloatProvider(null, 2.5);
		assertThat(provider).isInstanceOf(ConstantFloat.class);
		assertThat(provider.min()).isEqualTo(2.5F);
		assertThat(provider.max()).isEqualTo(2.5F);
	}

	@Test
	void wrapFloatProviderFromRangeList() {
		var provider = MiscWrappers.wrapFloatProvider(null, List.of(1.0F, 4.0F));
		assertThat(provider).isInstanceOf(UniformFloat.class);
		assertThat(provider.min()).isEqualTo(1.0F);
		assertThat(provider.max()).isEqualTo(4.0F);
	}

	@Test
	void wrapNumberProviderFromNumber() {
		var provider = MiscWrappers.wrapNumberProvider(null, 3);
		assertThat(provider).isInstanceOf(UniformGenerator.class);
	}

	@Test
	void wrapVec3ReturnsSameVec() {
		var vec = new Vec3(1.0, 2.0, 3.0);
		assertThat(MiscWrappers.wrapVec3(null, vec)).isEqualTo(vec);
	}

	@Test
	void wrapVec3FromList() {
		assertThat(MiscWrappers.wrapVec3(null, List.of(1.5, 2.5, 3.5))).isEqualTo(new Vec3(1.5, 2.5, 3.5));
	}

	@Test
	void wrapVec3FromBlockPosCentersCoordinates() {
		assertThat(MiscWrappers.wrapVec3(null, new BlockPos(1, 2, 3))).isEqualTo(new Vec3(1.5, 2.5, 3.5));
	}

	@Test
	void wrapBlockPosReturnsSamePos() {
		var pos = new BlockPos(4, 5, 6);
		assertThat(MiscWrappers.wrapBlockPos(null, pos)).isEqualTo(pos);
	}

	@Test
	void wrapBlockPosFromList() {
		assertThat(MiscWrappers.wrapBlockPos(null, List.of(4, 5, 6))).isEqualTo(new BlockPos(4, 5, 6));
	}

	@Test
	void wrapBlockPosFromVec3Floors() {
		assertThat(MiscWrappers.wrapBlockPos(null, new Vec3(1.2, 2.9, 3.5))).isEqualTo(new BlockPos(1, 2, 3));
	}
}
