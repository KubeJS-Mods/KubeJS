package dev.latvian.kubejs.fluid;

import me.shedaniel.architectury.fluid.FluidStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public class EmptyFluidStackJS extends FluidStackJS {
	public static final EmptyFluidStackJS INSTANCE = new EmptyFluidStackJS();

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public String getId() {
		return "minecraft:empty";
	}

	@Override
	public Fluid getFluid() {
		return Fluids.EMPTY;
	}

	@Nullable
	@Override
	public FluidStack getFluidStack() {
		return null;
	}

	@Override
	public int getAmount() {
		return 0;
	}

	@Override
	public void setAmount(int amount) {
	}

	@Override
	@Nullable
	public CompoundTag getNbt() {
		return null;
	}

	@Override
	public void setNbt(@Nullable CompoundTag nbt) {
	}

	@Override
	public FluidStackJS copy() {
		return this;
	}

	@Override
	public boolean equals(Object o) {
		return FluidStackJS.of(o).isEmpty();
	}

	@Override
	public boolean strongEquals(Object o) {
		return FluidStackJS.of(o).isEmpty();
	}

	@Override
	public String toString() {
		return "Fluid.empty";
	}

	@Override
	public void setChance(double c) {
	}

	@Override
	public double getChance() {
		return Double.NaN;
	}

	@Override
	public boolean hasChance() {
		return false;
	}
}