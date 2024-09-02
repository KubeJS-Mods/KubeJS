package dev.latvian.mods.kubejs.block.entity;

import net.minecraft.core.Direction;

import java.util.EnumSet;

public record BlockEntityAttachmentInfo(String id, BlockEntityAttachmentType type, int index, EnumSet<Direction> directions, BlockEntityAttachmentFactory factory) {
	@Override
	public String toString() {
		return id;
	}
}
