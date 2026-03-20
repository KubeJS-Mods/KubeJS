package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.plugin.builtin.wrapper.DirectionWrapper;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;
import java.util.Set;

public class FluidTankAttachment {
	public record Config(String id, EnumSet<Direction> directions, int capacity, @Nullable FluidIngredient inputFilter) {
	}

	public static Config createConfig(String id, Set<Direction> directions, int capacity, @Nullable FluidIngredient inputFilter) {
		return new Config(
			id,
			directions == null || directions.isEmpty() ? DirectionWrapper.EMPTY_SET : EnumSet.copyOf(directions),
			Math.max(0, capacity),
			inputFilter
		);
	}

	public static final class Wrapped extends SnapshotJournal<FluidStack> implements ResourceHandler<FluidResource> {
		private final KubeBlockEntity entity;
		private final String id;
		private final int capacity;
		private final @Nullable FluidIngredient inputFilter;

		private FluidStack stack = FluidStack.EMPTY;

		public Wrapped(KubeBlockEntity entity, Config config) {
			this.entity = entity;
			this.id = config.id();
			this.capacity = config.capacity();
			this.inputFilter = config.inputFilter();
		}

		public FluidStack getFluid() {
			return stack;
		}

		public void setFluid(FluidStack newStack) {
			this.stack = newStack == null ? FluidStack.EMPTY : newStack;
			syncToAttachment();
		}

		@Override
		public int size() {
			return 1;
		}

		@Override
		public FluidResource getResource(int index) {
			if (index != 0) {
				throw new IndexOutOfBoundsException(index);
			}
			return FluidResource.of(stack);
		}

		@Override
		public long getAmountAsLong(int index) {
			if (index != 0) {
				throw new IndexOutOfBoundsException(index);
			}
			return stack.getAmount();
		}

		@Override
		public long getCapacityAsLong(int index, FluidResource resource) {
			if (index != 0) {
				throw new IndexOutOfBoundsException(index);
			}
			return isValid(index, resource) ? capacity : 0L;
		}

		@Override
		public boolean isValid(int index, FluidResource resource) {
			if (index != 0) {
				throw new IndexOutOfBoundsException(index);
			}
			TransferPreconditions.checkNonEmpty(resource);
			if (inputFilter == null) {
				return true;
			}
			return inputFilter.test(resource.toStack(FluidType.BUCKET_VOLUME));
		}

		@Override
		public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
			if (index != 0) {
				throw new IndexOutOfBoundsException(index);
			}
			TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
			if (amount == 0) {
				return 0;
			}
			if (!isValid(index, resource)) {
				return 0;
			}

			updateSnapshots(transaction);

			if (stack.isEmpty()) {
				int inserted = Math.min(amount, capacity);
				stack = resource.toStack(inserted);
				return inserted;
			}

			FluidResource current = FluidResource.of(stack);
			if (!current.equals(resource)) {
				return 0;
			}

			int space = Math.max(0, capacity - stack.getAmount());
			int inserted = Math.min(amount, space);
			if (inserted <= 0) {
				return 0;
			}

			stack = stack.copyWithAmount(stack.getAmount() + inserted);
			return inserted;
		}

		@Override
		public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
			if (index != 0) {
				throw new IndexOutOfBoundsException(index);
			}
			TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
			if (amount == 0) {
				return 0;
			}
			if (stack.isEmpty()) {
				return 0;
			}

			FluidResource current = FluidResource.of(stack);
			if (!current.equals(resource)) {
				return 0;
			}

			updateSnapshots(transaction);

			int extracted = Math.min(amount, stack.getAmount());
			int remaining = stack.getAmount() - extracted;
			stack = remaining <= 0 ? FluidStack.EMPTY : stack.copyWithAmount(remaining);
			return extracted;
		}

		@Override
		protected FluidStack createSnapshot() {
			return stack.copy();
		}

		@Override
		protected void revertToSnapshot(FluidStack snapshot) {
			stack = snapshot;
		}

		@Override
		protected void onRootCommit(FluidStack originalState) {
			if (!FluidStack.matches(originalState, stack)) {
				syncToAttachment();
				entity.save();
			}
		}

		public void syncFromAttachment() {
			var map = entity.getData(KubeJSAttachmentTypes.FLUID.get());
			var loaded = map.get(id);
			stack = loaded != null ? loaded : FluidStack.EMPTY;
		}

		public void syncToAttachment() {
			var map = entity.getData(KubeJSAttachmentTypes.FLUID.get());
			if (!stack.isEmpty()) {
				map.put(id, stack);
			} else {
				map.remove(id);
			}
		}
	}
}
