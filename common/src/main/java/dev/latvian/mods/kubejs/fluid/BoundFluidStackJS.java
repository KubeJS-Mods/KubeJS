package dev.latvian.mods.kubejs.fluid;

import dev.architectury.fluid.FluidStack;
import dev.architectury.registry.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

public class BoundFluidStackJS extends FluidStackJS {
	private final FluidStack fluidStack;

	public BoundFluidStackJS(FluidStack fs) {
		fluidStack = fs;
	}

	@Override
	public String getId() {
		return Registries.getId(fluidStack.getFluid(), Registry.FLUID_REGISTRY).toString();
	}

	@Override
	public Fluid getFluid() {
		return fluidStack.getFluid();
	}

	@Override
	public FluidStack getFluidStack() {
		return fluidStack;
	}

	@Override
	public long getAmount() {
		return fluidStack.getAmount();
	}

	@Override
	public void setAmount(long amount) {
		fluidStack.setAmount(amount);
	}

	@Override
	@Nullable
	public CompoundTag getNbt() {
		return fluidStack.getTag();
	}

	@Override
	public void setNbt(@Nullable CompoundTag nbt) {
		fluidStack.setTag(nbt);
	}

	@Override
	public FluidStackJS copy(long amount) {
		var fs = fluidStack.copy();
		fs.setAmount(amount);
		return new BoundFluidStackJS(fs);
	}
}
