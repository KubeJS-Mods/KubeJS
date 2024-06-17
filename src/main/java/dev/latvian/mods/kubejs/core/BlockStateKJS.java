package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.bindings.event.BlockEvents;
import dev.latvian.mods.kubejs.block.RandomTickKubeEvent;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

@RemapPrefixForJS("kjs$")
public interface BlockStateKJS extends RegistryObjectKJS<Block> {
	@Override
	default RegistryInfo<Block> kjs$getKubeRegistry() {
		return RegistryInfo.BLOCK;
	}

	@Override
	default Holder<Block> kjs$asHolder() {
		return ((BlockBehaviour.BlockStateBase) this).getBlock().kjs$asHolder();
	}

	@Override
	default ResourceKey<Block> kjs$getRegistryKey() {
		return ((BlockBehaviour.BlockStateBase) this).getBlock().kjs$getRegistryKey();
	}

	@Override
	default String kjs$getId() {
		return ((BlockBehaviour.BlockStateBase) this).getBlock().kjs$getId();
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

	default boolean kjs$randomTickOverride(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (BlockEvents.RANDOM_TICK.hasListeners(state.kjs$getRegistryKey())) {
			return BlockEvents.RANDOM_TICK.post(ScriptType.SERVER, state.kjs$getRegistryKey(), new RandomTickKubeEvent(level, pos, state, random)).interruptFalse();
		}

		return false;
	}
}
