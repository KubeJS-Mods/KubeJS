package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.block.RandomTickCallbackJS;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Consumer;

@RemapPrefixForJS("kjs$")
public interface BlockKJS extends BlockBuilderProvider, WithRegistryKeyKJS<Block> {
	default void kjs$setBlockBuilder(BlockBuilder b) {
		throw new NoMixinException();
	}

	@Override
	default RegistryInfo<Block> kjs$getKubeRegistry() {
		return RegistryInfo.BLOCK;
	}

	default CompoundTag kjs$getTypeData() {
		throw new NoMixinException();
	}

	default void kjs$setHasCollision(boolean v) {
		throw new NoMixinException();
	}

	default void kjs$setExplosionResistance(float v) {
		throw new NoMixinException();
	}

	default void kjs$setIsRandomlyTicking(boolean v) {
		throw new NoMixinException();
	}

	default void kjs$setRandomTickCallback(Consumer<RandomTickCallbackJS> callback) {
		throw new NoMixinException();
	}

	default void kjs$setSoundType(SoundType v) {
		throw new NoMixinException();
	}

	default void kjs$setFriction(float v) {
		throw new NoMixinException();
	}

	default void kjs$setSpeedFactor(float v) {
		throw new NoMixinException();
	}

	default void kjs$setJumpFactor(float v) {
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
}
