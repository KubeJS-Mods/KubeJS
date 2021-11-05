package dev.latvian.kubejs.fluid;

import me.shedaniel.architectury.fluid.FluidStack;
import me.shedaniel.architectury.utils.Fraction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public class UnboundFluidStackJS extends FluidStackJS {
	private final ResourceLocation fluidRL;
	private final String fluid;
	private int amount;
	private CompoundTag nbt;
	private FluidStack cached;

	public UnboundFluidStackJS(ResourceLocation f) {
		fluidRL = f;
		fluid = fluidRL.toString();
		amount = FluidStack.bucketAmount().intValue();
		nbt = null;
		cached = null;
	}

	@Override
	public String getId() {
		return fluid;
	}

	@Override
	public boolean isEmpty() {
		return super.isEmpty() || getFluid() == Fluids.EMPTY;
	}

	@Override
	public FluidStack getFluidStack() {
		if (cached == null) {
			cached = FluidStack.create(this::getFluid, Fraction.ofWhole(amount), nbt);
		}

		return cached;
	}

	@Override
	public int getAmount() {
		return amount;
	}

	@Override
	public void setAmount(int a) {
		amount = a;
		cached = null;
	}

	@Override
	@Nullable
	public CompoundTag getNbt() {
		return nbt;
	}

	@Override
	public void setNbt(@Nullable CompoundTag n) {
		nbt = n;
		cached = null;
	}

	@Override
	public FluidStackJS copy() {
		UnboundFluidStackJS fs = new UnboundFluidStackJS(fluidRL);
		fs.amount = amount;
		fs.nbt = nbt == null ? null : nbt.copy();
		return fs;
	}
}