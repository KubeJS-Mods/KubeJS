package dev.latvian.mods.kubejs.fluid;

import dev.architectury.fluid.FluidStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public class UnboundFluidStackJS extends FluidStackJS {
	private final ResourceLocation fluidRL;
	private final String fluid;
	private long amount;
	private CompoundTag nbt;
	private FluidStack cached;

	public UnboundFluidStackJS(ResourceLocation f) {
		fluidRL = f;
		fluid = fluidRL.toString();
		amount = FluidStack.bucketAmount();
		nbt = null;
		cached = null;
	}

	@Override
	public String getId() {
		return fluid;
	}

	@Override
	public boolean kjs$isEmpty() {
		return super.kjs$isEmpty() || getFluid() == Fluids.EMPTY;
	}

	@Override
	public FluidStack getFluidStack() {
		if (cached == null) {
			cached = FluidStack.create(this::getFluid, amount, nbt);
		}

		return cached;
	}

	@Override
	public long kjs$getAmount() {
		return amount;
	}

	@Override
	public void setAmount(long a) {
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
	public FluidStackJS kjs$copy(long amount) {
		var fs = new UnboundFluidStackJS(fluidRL);
		fs.amount = amount;
		fs.nbt = nbt == null ? null : nbt.copy();
		return fs;
	}
}