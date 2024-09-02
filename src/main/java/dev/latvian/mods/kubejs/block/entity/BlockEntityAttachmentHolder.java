package dev.latvian.mods.kubejs.block.entity;

public record BlockEntityAttachmentHolder(BlockEntityAttachmentInfo info, BlockEntityAttachment attachment) {
	public static final BlockEntityAttachmentHolder[] EMPTY_ARRAY = new BlockEntityAttachmentHolder[0];

	@Override
	public String toString() {
		return info.id();
	}
}
