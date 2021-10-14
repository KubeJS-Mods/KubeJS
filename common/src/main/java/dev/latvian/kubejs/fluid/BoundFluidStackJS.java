package dev.latvian.kubejs.fluid;

import dev.architectury.fluid.FluidStack;
import dev.architectury.registry.registries.Registries;
import dev.latvian.kubejs.util.MapJS;
import net.minecraft.core.Registry;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
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
	public MapJS getNbt() {
		return MapJS.of(fluidStack.getTag());
	}

	@Override
	public void setNbt(@Nullable Object nbt) {
		fluidStack.setTag(MapJS.nbt(nbt));
	}

	@Override
	public FluidStackJS copy() {
		return new BoundFluidStackJS(fluidStack.copy());
	}

	@Override
	public void onChanged(@Nullable MapJS o) {
		setNbt(o);
	}
}
