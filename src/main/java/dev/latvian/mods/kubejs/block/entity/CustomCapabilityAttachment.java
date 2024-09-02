package dev.latvian.mods.kubejs.block.entity;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public record CustomCapabilityAttachment(BlockCapability<?, ?> capability, Object data) implements BlockEntityAttachment, INBTSerializable<Tag> {
	public static final BlockEntityAttachmentType TYPE = new BlockEntityAttachmentType("custom_capability", Factory.class);

	public record Factory(BlockCapability<?, ?> type, Supplier<?> dataFactory) implements BlockEntityAttachmentFactory {
		@Override
		public BlockEntityAttachment create(KubeBlockEntity entity) {
			return new CustomCapabilityAttachment(type, dataFactory.get());
		}

		@Override
		public List<BlockCapability<?, ?>> getCapabilities() {
			return List.of(type);
		}
	}

	@Override
	public Object getExposedObject() {
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

	@Override
	public Tag serializeNBT(HolderLookup.Provider registries) {
		return data instanceof INBTSerializable<?> s ? s.serializeNBT(registries) : null;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider registries, Tag tag) {
		if (data instanceof INBTSerializable s) {
			s.deserializeNBT(registries, tag);
		}
	}
}
