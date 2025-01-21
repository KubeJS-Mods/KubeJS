package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class FluidTankAttachment implements BlockEntityAttachment {
	public static final BlockEntityAttachmentType TYPE = new BlockEntityAttachmentType(KubeJS.id("fluid_tank"), Factory.class);

	public record Factory(int capacity, Optional<FluidIngredient> inputFilter) implements BlockEntityAttachmentFactory {
		private static final Predicate<FluidStack> ALWAYS_TRUE = stack -> true;

		@Override
		public BlockEntityAttachment create(BlockEntityAttachmentInfo info, KubeBlockEntity entity) {
			return new FluidTankAttachment(entity, capacity, inputFilter.isEmpty() ? ALWAYS_TRUE : inputFilter.get());
		}

		@Override
		public List<BlockCapability<?, ?>> getCapabilities() {
			return List.of(Capabilities.EnergyStorage.BLOCK);
		}
	}

	public static class Wrapped extends FluidTank {
		private final FluidTankAttachment attachment;

		public Wrapped(FluidTankAttachment attachment, int capacity, Predicate<FluidStack> inputFilter) {
			super(capacity, inputFilter);
			this.attachment = attachment;
		}

		@Override
		protected void onContentsChanged() {
			attachment.entity.save();
		}
	}

	public final KubeBlockEntity entity;
	public final Wrapped fluidTank;

	public FluidTankAttachment(KubeBlockEntity entity, int capacity, Predicate<FluidStack> filter) {
		this.entity = entity;
		this.fluidTank = new Wrapped(this, capacity, filter);
	}

	@Override
	public Object getWrappedObject() {
		return fluidTank;
	}

	@Override
	@Nullable
	public <CAP, SRC> CAP getCapability(BlockCapability<CAP, SRC> capability) {
		if (capability == Capabilities.FluidHandler.BLOCK) {
			return (CAP) fluidTank;
		}

		return null;
	}

	@Override
	@Nullable
	public Tag serialize(HolderLookup.Provider registries) {
		return fluidTank.getFluid().isEmpty() ? null : fluidTank.getFluid().save(registries);
	}

	@Override
	public void deserialize(HolderLookup.Provider registries, @Nullable Tag tag) {
		fluidTank.setFluid(tag == null ? FluidStack.EMPTY : FluidStack.parse(registries, tag).orElse(FluidStack.EMPTY));
	}
}
