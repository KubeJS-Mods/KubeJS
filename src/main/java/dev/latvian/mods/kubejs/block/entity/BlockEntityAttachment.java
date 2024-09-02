package dev.latvian.mods.kubejs.block.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

public interface BlockEntityAttachment {
	default Object getExposedObject() {
		return this;
	}

	@Nullable
	default <CAP, SRC> CAP getCapability(BlockCapability<CAP, SRC> capability) {
		return null;
	}

	default void onRemove(ServerLevel level, KubeBlockEntity blockEntity, BlockState newState) {
	}

	default void tick() {
	}
}
