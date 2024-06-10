package dev.latvian.mods.kubejs.fluid;

import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.ArrayList;
import java.util.List;

public interface FluidWrapper {
	TypeInfo TYPE_INFO = TypeInfo.of(FluidStack.class);

	static FluidStack wrap(Context cx, Object o) {
		return FluidStack.EMPTY; // FIXME
	}

	static FluidStack of(FluidStack o) {
		return o;
	}

	static FluidStack of(FluidStack o, int amount) {
		o.setAmount(amount);
		return o;
	}

	static FluidStack of(FluidStack o, DataComponentMap components) {
		o.applyComponents(components);
		return o;
	}

	static FluidStack of(FluidStack o, int amount, DataComponentMap components) {
		o.setAmount(amount);
		o.applyComponents(components);
		return o;
	}

	static FluidStack water() {
		return water(FluidType.BUCKET_VOLUME);
	}

	static FluidStack lava() {
		return lava(FluidType.BUCKET_VOLUME);
	}

	static FluidStack water(int amount) {
		return new FluidStack(Fluids.WATER, amount);
	}

	static FluidStack lava(int amount) {
		return new FluidStack(Fluids.LAVA, amount);
	}

	static Fluid getType(ResourceLocation id) {
		return RegistryInfo.FLUID.getValue(id);
	}

	static List<String> getTypes() {
		var types = new ArrayList<String>();

		for (var entry : RegistryInfo.FLUID.entrySet()) {
			types.add(entry.getKey().location().toString());
		}

		return types;
	}

	static FluidStack getEmpty() {
		return FluidStack.EMPTY;
	}

	static boolean exists(ResourceLocation id) {
		return RegistryInfo.FLUID.hasValue(id);
	}

	static ResourceLocation getId(Fluid fluid) {
		return RegistryInfo.FLUID.getId(fluid);
	}
}