package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.KubeJS;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public record CustomCapabilityAttachment(BlockCapability<?, ?> capability, Object data) implements BlockEntityAttachment {
	public static final BlockEntityAttachmentType TYPE = new BlockEntityAttachmentType(KubeJS.id("custom_capability"), Factory.class);

	public record Factory(BlockCapability<?, ?> type, Supplier<?> dataFactory) implements BlockEntityAttachmentFactory {
		@Override
		public BlockEntityAttachment create(BlockEntityAttachmentInfo info, KubeBlockEntity entity) {
			return new CustomCapabilityAttachment(type, dataFactory.get());
		}

		@Override
		public List<BlockCapability<?, ?>> getCapabilities() {
			return List.of(type);
		}
	}

	@Override
	public Object getWrappedObject() {
		return data;
	}

	@Override
	@Nullable
	public <CAP, SRC> CAP getCapability(BlockCapability<CAP, SRC> c) {
		if (c == capability) {
			return (CAP) data;
		}

		return null;
	}
}
