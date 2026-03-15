package dev.latvian.mods.kubejs.block.entity;

import com.mojang.serialization.DataResult;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.util.Cast;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class FluidTankAttachment implements BlockEntityAttachment {
	public static final BlockEntityAttachmentType TYPE = new BlockEntityAttachmentType(KubeJS.id("fluid_tank"), Factory.class);

	public record Factory(int capacity, Optional<FluidIngredient> inputFilter) implements BlockEntityAttachmentFactory {
		@Override
		public BlockEntityAttachment create(BlockEntityAttachmentInfo info, KubeBlockEntity entity) {
			return new FluidTankAttachment(entity, capacity, inputFilter.orElse(null));
		}

		@Override
		public List<BlockCapability<?, ?>> getCapabilities() {
			return List.of(Capabilities.Fluid.BLOCK);
		}
	}

	public final KubeBlockEntity entity;
	public final Wrapped fluidTank;

	public FluidTankAttachment(KubeBlockEntity entity, int capacity, @Nullable FluidIngredient inputFilter) {
		this.entity = entity;
		this.fluidTank = new Wrapped(this, capacity, inputFilter);
	}

	@Override
	public Object getWrappedObject() {
		return fluidTank;
	}

	@Override
	@Nullable
	public <CAP, SRC> CAP getCapability(BlockCapability<CAP, SRC> capability) {
		if (capability == Capabilities.Fluid.BLOCK) {
			return Cast.to(fluidTank);
		}
		return null;
	}

	@Override
	@Nullable
	public Tag serialize(HolderLookup.Provider registries) {
		var stack = fluidTank.getFluid();
		if (stack.isEmpty()) {
			return null;
		}
		return FluidStack.OPTIONAL_CODEC.encodeStart(NbtOps.INSTANCE, stack).result().orElse(null);
	}

	@Override
	public void deserialize(HolderLookup.Provider registries, @Nullable Tag tag) {
		if (tag == null) {
			fluidTank.setFluid(FluidStack.EMPTY);
			return;
		}
		DataResult<FluidStack> parsed = FluidStack.OPTIONAL_CODEC.parse(NbtOps.INSTANCE, tag);
		fluidTank.setFluid(parsed.result().orElse(FluidStack.EMPTY));
	}

	public static final class Wrapped extends SnapshotJournal<FluidStack> implements ResourceHandler<FluidResource> {
		private final FluidTankAttachment attachment;
		private final int capacity;
		private final @Nullable FluidIngredient inputFilter;

		private FluidStack stack = FluidStack.EMPTY;

		public Wrapped(FluidTankAttachment attachment, int capacity, @Nullable FluidIngredient inputFilter) {
			this.attachment = attachment;
			this.capacity = capacity;
			this.inputFilter = inputFilter;
		}

		public FluidStack getFluid() {
			return stack;
		}

		public void setFluid(FluidStack newStack) {
			this.stack = newStack == null ? FluidStack.EMPTY : newStack;
			attachment.entity.save();
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
				attachment.entity.save();
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
			attachment.entity.save();
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
			attachment.entity.save();
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
		}
	}
}
