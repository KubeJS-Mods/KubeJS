package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.bindings.BlockWrapper;
import dev.latvian.mods.kubejs.bindings.event.BlockEvents;
import dev.latvian.mods.kubejs.block.RandomTickKubeEvent;
import dev.latvian.mods.kubejs.recipe.match.Replaceable;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

@RemapPrefixForJS("kjs$")
public interface BlockStateKJS extends RegistryObjectKJS<Block>, Replaceable {
	@Override
	default ResourceKey<Registry<Block>> kjs$getRegistryId() {
		return Registries.BLOCK;
	}

	@Override
	default Registry<Block> kjs$getRegistry() {
		return BuiltInRegistries.BLOCK;
	}

	@Override
	default Holder<Block> kjs$asHolder() {
		return ((BlockBehaviour.BlockStateBase) this).getBlock().kjs$asHolder();
	}

	@Override
	default ResourceKey<Block> kjs$getKey() {
		return ((BlockBehaviour.BlockStateBase) this).getBlock().kjs$getKey();
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
		if (BlockEvents.RANDOM_TICK.hasListeners(state.kjs$getKey())) {
			return BlockEvents.RANDOM_TICK.post(ScriptType.SERVER, state.kjs$getKey(), new RandomTickKubeEvent(level, pos, state, random)).interruptFalse();
		}

		return false;
	}

	@Override
	default Object replaceThisWith(Context cx, Object with) {
		return with instanceof BlockState state ? state : with instanceof Block block ? block.defaultBlockState() : cx.jsToJava(with, BlockWrapper.STATE_TYPE_INFO);
	}
}
