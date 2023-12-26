package dev.latvian.mods.kubejs.block.entity;

public record BlockEntityAttachmentHolder(int index, BlockEntityAttachment.Factory factory) {
	@Override
	public String toString() {
		return "attachment_" + index;
	}
}
