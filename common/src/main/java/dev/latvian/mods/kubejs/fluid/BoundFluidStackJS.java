package dev.latvian.mods.kubejs.fluid;

import dev.architectury.fluid.FluidStack;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
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
		return RegistryInfo.FLUID.getId(fluidStack.getFluid()).toString();
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
	public long kjs$getAmount() {
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
	public FluidStackJS kjs$copy(long amount) {
		var fs = fluidStack.copy();
		fs.setAmount(amount);
		return new BoundFluidStackJS(fs);
	}
}
