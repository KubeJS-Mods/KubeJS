package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.rhino.Context;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public interface BlockEntityAttachment {
	BlockEntityAttachment[] EMPTY_ARRAY = new BlockEntityAttachment[0];

	interface Factory {
		BlockEntityAttachment create(BlockEntityJS entity);
	}

	interface FactoryProvider {
		Factory createFactory(Context cx, Map<String, Object> map);
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
