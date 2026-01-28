package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.resources.Identifier;

@FunctionalInterface
public interface BlockEntityAttachmentRegistry {
	default void register(Identifier id, Class<? extends BlockEntityAttachmentFactory> factory) {
		register(new BlockEntityAttachmentType(id, TypeInfo.of(factory)));
	}

	void register(BlockEntityAttachmentType type);
}
