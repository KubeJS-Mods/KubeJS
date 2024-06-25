package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.bindings.BlockWrapper;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.recipe.match.Replaceable;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

@RemapPrefixForJS("kjs$")
public interface BlockKJS extends BlockBuilderProvider, RegistryObjectKJS<Block>, Replaceable {
	@Override
	default RegistryInfo<Block> kjs$getKubeRegistry() {
		return RegistryInfo.BLOCK;
	}

	default void kjs$setBlockBuilder(BlockBuilder b) {
		throw new NoMixinException();
	}

	default void kjs$setNameKey(String key) {
		throw new NoMixinException();
	}

	default void kjs$setDestroySpeed(float v) {
		for (var state : kjs$getBlockStates()) {
			state.kjs$setDestroySpeed(v);
		}
	}

	default void kjs$setLightEmission(int v) {
		for (var state : kjs$getBlockStates()) {
			state.kjs$setLightEmission(v);
		}
	}

	default void kjs$setRequiresTool(boolean v) {
		for (var state : kjs$getBlockStates()) {
			state.kjs$setRequiresTool(v);
		}
	}

	default List<BlockState> kjs$getBlockStates() {
		return this instanceof Block block ? block.getStateDefinition().getPossibleStates() : List.of();
	}

	@Override
	default Object replaceThisWith(Context cx, Object with) {
		return with instanceof Block block ? block : with instanceof BlockState state ? state.getBlock() : cx.jsToJava(with, BlockWrapper.TYPE_INFO);
	}
}
