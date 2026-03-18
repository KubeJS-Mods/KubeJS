package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.level.LevelBlock;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
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
			return List.of(Capabilities.Item.BLOCK);
		}

	}

	public static class Wrapped extends ItemStacksResourceHandler implements InventoryKJS {
		protected final InventoryAttachment attachment;
		protected final NonNullList<ItemStack> stacks;

		public Wrapped(InventoryAttachment attachment) {
			this(attachment, NonNullList.withSize(attachment.width * attachment.height, ItemStack.EMPTY));
		}

		public Wrapped(InventoryAttachment attachment, NonNullList<ItemStack> stacks) {
			super(stacks);
			this.attachment = attachment;
			this.stacks = stacks;
		}

		public NonNullList<ItemStack> stacks() {
			return stacks;
		}

		@Override
		protected void onContentsChanged(int index, ItemStack previousContents) {
			attachment.blockEntity.save();
		}

		@Override
		public boolean isValid(int index, ItemResource resource) {
			if (attachment.inputFilter == null) {
				return true;
			}

			return attachment.inputFilter.test(resource.toStack(1));
		}

		@Override
		public boolean kjs$isMutable() {
			return true;
		}

		@Override
		public int kjs$getSlots() {
			return size();
		}

		@Override
		public ItemStack kjs$getStackInSlot(int slot) {
			var resource = getResource(slot);
			if (resource.isEmpty()) {
				return ItemStack.EMPTY;
			}
			return resource.toStack(getAmountAsInt(slot));
		}

		@Override
		public void kjs$setStackInSlot(int slot, ItemStack stack) {
			var prev = stacks.get(slot);
			stacks.set(slot, stack);
			onContentsChanged(slot, prev);
		}

		@Override
		public ItemStack kjs$insertItem(int slot, ItemStack stack, boolean simulate) {
			if (stack.isEmpty()) {
				return stack;
			}
			var resource = ItemResource.of(stack);
			try (var tx = Transaction.openRoot()) {
				int inserted = insert(slot, resource, stack.getCount(), tx);
				if (!simulate) {
					tx.commit();
				}
				int remaining = stack.getCount() - inserted;
				return remaining <= 0 ? ItemStack.EMPTY : stack.copyWithCount(remaining);
			}
		}

		@Override
		public ItemStack kjs$extractItem(int slot, int amount, boolean simulate) {
			if (amount <= 0) {
				return ItemStack.EMPTY;
			}
			var resource = getResource(slot);
			if (resource.isEmpty()) {
				return ItemStack.EMPTY;
			}
			try (var tx = Transaction.openRoot()) {
				int extracted = extract(slot, resource, amount, tx);
				if (!simulate) {
					tx.commit();
				}
				return extracted <= 0 ? ItemStack.EMPTY : resource.toStack(extracted);
			}
		}

		@Override
		public int kjs$getSlotLimit(int slot) {
			return getCapacityAsInt(slot, ItemResource.EMPTY);
		}

		@Override
		public boolean kjs$isItemValid(int slot, ItemStack stack) {
			if (stack.isEmpty()) {
				return false;
			}
			return isValid(slot, ItemResource.of(stack));
		}

		@Override
		public int kjs$getWidth() {
			return attachment.width;
		}

		@Override
		public int kjs$getHeight() {
			return attachment.height;
		}

		@Override
		@Nullable
		public LevelBlock kjs$getBlock(Level level) {
			return level.kjs$getBlock(attachment.blockEntity);
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
		if (capability == Capabilities.Item.BLOCK) {
			return (CAP) inventory;
		}

		return null;
	}

	@Override
	public ListTag serialize(HolderLookup.Provider registries) {
		var list = new ListTag();
		var ops = registries.createSerializationContext(NbtOps.INSTANCE);

		for (int i = 0; i < width * height; i++) {
			var stack = inventory.stacks().get(i);

			if (!stack.isEmpty()) {
				var tag = ItemStack.CODEC.encodeStart(ops, stack).result().orElse(null);

				if (tag instanceof CompoundTag itemTag) {
					itemTag.putInt("slot", i);
					list.add(itemTag);
				}
			}
		}

		return list;
	}

	@Override
	public void deserialize(HolderLookup.Provider registries, Tag tag) {
		inventory.stacks().clear();
		for (int i = 0; i < width * height; i++) {
			inventory.stacks().add(ItemStack.EMPTY);
		}

		if (tag instanceof ListTag list) {
			var ops = registries.createSerializationContext(NbtOps.INSTANCE);

			for (int i = 0; i < list.size(); i++) {
				var itemTagOpt = list.getCompound(i);
				if (itemTagOpt.isEmpty()) {
					continue;
				}

				var itemTag = itemTagOpt.get();
				var slotOpt = itemTag.getInt("slot");
				if (slotOpt.isEmpty()) {
					continue;
				}

				int slot = slotOpt.get();
				if (slot >= 0 && slot < width * height) {
					var decoded = ItemStack.CODEC.parse(ops, itemTag).result().orElse(ItemStack.EMPTY);
					inventory.stacks().set(slot, decoded);
				}
			}
		}
	}

	@Override
	public void onRemove(ServerLevel level, KubeBlockEntity blockEntity, BlockState newState) {
		Containers.dropContents(blockEntity.getLevel(), blockEntity.getBlockPos(), inventory.stacks());
	}
}
