package dev.latvian.mods.kubejs.block.entity;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

public interface BlockEntityAttachment {
	default Object getWrappedObject() {
		return this;
	}

	@Nullable
	default <CAP, SRC> CAP getCapability(BlockCapability<CAP, SRC> capability) {
		return null;
	}

	@Nullable
	default Tag serialize(HolderLookup.Provider registries) {
		if (getWrappedObject() instanceof INBTSerializable<?> s) {
			return s.serializeNBT(registries);
		}

		return null;
	}

	default void deserialize(HolderLookup.Provider registries, @Nullable Tag tag) {
		if (tag != null && getWrappedObject() instanceof INBTSerializable s) {
			s.deserializeNBT(registries, tag);
		}
	}

	default void onRemove(ServerLevel level, KubeBlockEntity blockEntity, BlockState newState) {
	}

	default void serverTick() {
	}
}
