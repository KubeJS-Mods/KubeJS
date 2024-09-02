package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.rhino.type.TypeInfo;

@FunctionalInterface
public interface BlockEntityAttachmentRegistry {
	default void register(String name, Class<? extends BlockEntityAttachmentFactory> factory) {
		register(new BlockEntityAttachmentType(name, TypeInfo.of(factory)));
	}

	void register(BlockEntityAttachmentType type);
}
