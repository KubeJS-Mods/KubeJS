package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@RemapPrefixForJS("kjs$")
public interface BlockStateKJS extends WithRegistryKeyKJS<Block> {
	@Override
	default RegistryInfo<Block> kjs$getKubeRegistry() {
		return RegistryInfo.BLOCK;
	}

	@Override
	default ResourceKey<Block> kjs$getRegistryKey() {
		return ((BlockState) this).getBlock().kjs$getRegistryKey();
	}

	default void kjs$setDestroySpeed(float v) {
		throw new NoMixinException();
	}

	default void kjs$setRequiresTool(boolean v) {
		throw new NoMixinException();
	}

	default void kjs$setLightEmission(int v) {
		throw new NoMixinException();
	}
}
