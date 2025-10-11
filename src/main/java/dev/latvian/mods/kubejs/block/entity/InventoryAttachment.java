package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.item.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class InventoryAttachment implements BlockEntityAttachment {
	public static final BlockEntityAttachmentType TYPE = new BlockEntityAttachmentType(KubeJS.id("inventory"), Factory.class);

	public record Factory(int width, int height, Optional<ItemPredicate> inputFilter) implements BlockEntityAttachmentFactory {
		@Override
		public BlockEntityAttachment create(BlockEntityAttachmentInfo info, KubeBlockEntity entity) {
			return new InventoryAttachment(entity, width, height, inputFilter.orElse(null));
		}

		@Override
		public List<BlockCapability<?, ?>> getCapabilities() {
			return List.of(Capabilities.ItemHandler.BLOCK);
		}
	}

	public static class Wrapped extends ItemStackHandler implements InventoryKJS {
		protected final InventoryAttachment attachment;

		public Wrapped(InventoryAttachment attachment) {
			super(attachment.width * attachment.height);
			this.attachment = attachment;
		}

		public NonNullList<ItemStack> stacks() {
			return stacks;
		}

		@Override
		protected void onContentsChanged(int slot) {
			attachment.blockEntity.save();
		}

		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			return (attachment.inputFilter == null || attachment.inputFilter.test(stack)) && super.isItemValid(slot, stack);
		}

		@Override
		public int kjs$getWidth() {
			return attachment.width;
		}

		@Override
		public int kjs$getHeight() {
			return attachment.height;
		}
	}

	public final int width, height;
	public final KubeBlockEntity blockEntity;
	public final ItemPredicate inputFilter;
	public final Wrapped inventory;

	public InventoryAttachment(KubeBlockEntity blockEntity, int width, int height, @Nullable ItemPredicate inputFilter) {
		this.width = width;
		this.height = height;
		this.blockEntity = blockEntity;
		this.inputFilter = inputFilter;
		this.inventory = createInventory();
	}

	protected Wrapped createInventory() {
		return new Wrapped(this);
	}

	@Override
	public Object getWrappedObject() {
		return inventory;
	}

	@Override
	@Nullable
	public <CAP, SRC> CAP getCapability(BlockCapability<CAP, SRC> capability) {
		if (capability == Capabilities.ItemHandler.BLOCK) {
			return (CAP) inventory;
		}

		return null;
	}

	@Override
	public ListTag serialize(HolderLookup.Provider registries) {
		var list = new ListTag();

		for (int i = 0; i < width * height; i++) {
			var stack = inventory.stacks().get(i);

			if (!stack.isEmpty()) {
				var itemTag = (CompoundTag) stack.save(registries, new CompoundTag());
				itemTag.putByte("slot", (byte) i);
				list.add(itemTag);
			}
		}

		return list;
	}

	@Override
	public void deserialize(HolderLookup.Provider registries, Tag tag) {
		inventory.setSize(width * height);

		if (tag instanceof ListTag list) {
			for (int i = 0; i < list.size(); i++) {
				var itemTag = list.getCompound(i);
				var slot = itemTag.getByte("slot");

				if (slot >= 0 && slot < width * height) {
					inventory.stacks().set(slot, ItemStack.parse(registries, itemTag).orElse(ItemStack.EMPTY));
				}
			}
		}
	}

	@Override
	public void onRemove(ServerLevel level, KubeBlockEntity blockEntity, BlockState newState) {
		Containers.dropContents(blockEntity.getLevel(), blockEntity.getBlockPos(), inventory.stacks());
	}
}
