package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.fluid.FluidLike;
import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.recipe.match.Replaceable;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public interface FluidStackKJS extends Replaceable, FluidLike {
	default FluidStack kjs$self() {
		return (FluidStack) (Object) this;
	}

	@Override
	default int kjs$getAmount() {
		return kjs$self().getAmount();
	}

	@Override
	default boolean kjs$isEmpty() {
		return kjs$self().isEmpty();
	}

	@Override
	default Fluid kjs$getFluid() {
		return kjs$self().getFluid();
	}

	@Override
	default FluidLike kjs$copy(int amount) {
		return (FluidLike) (Object) kjs$self().copyWithAmount(amount);
	}

	@Override
	default Object replaceThisWith(Context cx, Object with) {
		var t = kjs$self();
		var r = FluidWrapper.wrap(RegistryAccessContainer.of(cx), with);

		if (!FluidStack.isSameFluidSameComponents(t, r)) {
			r.setAmount(t.getAmount());
			return r;
		}

		return this;
	}
}
