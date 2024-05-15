package dev.latvian.mods.kubejs.block.entity;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockEntityAttachment {
	BlockEntityAttachment[] EMPTY_ARRAY = new BlockEntityAttachment[0];

	interface Factory {
		BlockEntityAttachment create(BlockEntityJS entity);
	}

	// TODO: Replace with Codec

	default CompoundTag writeAttachment(HolderLookup.Provider registries) {
		return new CompoundTag();
	}

	default void readAttachment(HolderLookup.Provider registries, CompoundTag tag) {
	}

	default void onRemove(BlockState newState) {
	}
}
