package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.DirectionWrapper;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;
import java.util.Set;

public class InventoryAttachment {
	public record Config(String id, EnumSet<Direction> directions, int width, int height, @Nullable ItemPredicate inputFilter) {
		public int size() {
			return width * height;
		}
	}

	public static Config createConfig(String id, Set<Direction> directions, int width, int height, @Nullable ItemPredicate inputFilter) {
		return new Config(
			id,
			directions == null || directions.isEmpty() ? DirectionWrapper.EMPTY_SET : EnumSet.copyOf(directions),
			Math.max(1, width),
			Math.max(1, height),
			inputFilter
		);
	}

	public static void onRemove(ServerLevel level, KubeBlockEntity blockEntity) {
		for (var entry : blockEntity.inventoryWrappers.values()) {
			Containers.dropContents(level, blockEntity.getBlockPos(), entry.stacks());
		}
	}

	public static class Wrapped extends ItemStacksResourceHandler implements InventoryKJS {
		protected final KubeBlockEntity entity;
		protected final String id;
		protected final int width;
		protected final int height;
		protected final @Nullable ItemPredicate inputFilter;
		protected final NonNullList<ItemStack> stacks;

		public Wrapped(KubeBlockEntity entity, Config config) {
			this(entity, config, NonNullList.withSize(config.size(), ItemStack.EMPTY));
		}

		public Wrapped(KubeBlockEntity entity, Config config, NonNullList<ItemStack> stacks) {
			super(stacks);
			this.entity = entity;
			this.id = config.id();
			this.width = config.width();
			this.height = config.height();
			this.inputFilter = config.inputFilter();
			this.stacks = stacks;
		}

		public NonNullList<ItemStack> stacks() {
			return stacks;
		}

		@Override
		protected void onContentsChanged(int index, ItemStack previousContents) {
			syncToAttachment();
			entity.save();
		}

		@Override
		public boolean isValid(int index, ItemResource resource) {
			if (inputFilter == null) {
				return true;
			}

			return inputFilter.test(resource.toStack(1));
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
			return stacks.get(slot);
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
			return width;
		}

		@Override
		public int kjs$getHeight() {
			return height;
		}

		@Override
		@Nullable
		public LevelBlock kjs$getBlock(Level level) {
			return level.kjs$getBlock(entity);
		}

		public void syncFromAttachment() {
			var map = entity.getData(KubeJSAttachmentTypes.INVENTORY.get());
			var loadedStacks = map.get(id);
			if (loadedStacks != null) {
				for (int i = 0; i < stacks.size(); i++) {
					stacks.set(i, i < loadedStacks.size() ? loadedStacks.get(i) : ItemStack.EMPTY);
				}
			}
			map.put(id, stacks);
		}

		public void syncToAttachment() {
			var map = entity.getData(KubeJSAttachmentTypes.INVENTORY.get());
			map.put(id, stacks);
		}
	}
}
