package dev.latvian.mods.kubejs.block.entity;

import net.neoforged.neoforge.capabilities.BlockCapability;

import java.util.List;

public interface BlockEntityAttachmentFactory {
	BlockEntityAttachment create(KubeBlockEntity entity);

	default List<BlockCapability<?, ?>> getCapabilities() {
		return List.of();
	}

	default boolean isTicking() {
		return false;
	}
}
