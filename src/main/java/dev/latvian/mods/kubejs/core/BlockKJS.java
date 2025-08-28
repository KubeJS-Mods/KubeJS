package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.BlockWrapper;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.match.Replaceable;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

@RemapPrefixForJS("kjs$")
public interface BlockKJS extends BlockBuilderProvider, BlockBehaviourKJS, Replaceable {
	@Override
	default Block kjs$getBlock() {
		return (Block) this;
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
		return kjs$getBlock().getStateDefinition().getPossibleStates();
	}

	@Override
	default Object replaceThisWith(RecipeScriptContext cx, Object with) {
		return with instanceof Block block ? block : with instanceof BlockState state ? state.getBlock() : cx.cx().jsToJava(with, BlockWrapper.TYPE_INFO);
	}
}
