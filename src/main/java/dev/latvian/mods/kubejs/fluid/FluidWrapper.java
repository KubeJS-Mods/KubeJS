package dev.latvian.mods.kubejs.fluid;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.kubejs.bindings.DataComponentWrapper;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.crafting.EmptyFluidIngredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.ArrayList;
import java.util.List;

public interface FluidWrapper {
	TypeInfo TYPE_INFO = TypeInfo.of(FluidStack.class);
	TypeInfo FLUID_TYPE_INFO = TypeInfo.of(Fluid.class);
	TypeInfo INGREDIENT_TYPE_INFO = TypeInfo.of(FluidIngredient.class);
	TypeInfo SIZED_INGREDIENT_TYPE_INFO = TypeInfo.of(SizedFluidIngredient.class);

	static FluidStack wrap(RegistryAccessContainer registries, Object o) {
		if (o == null || o == FluidStack.EMPTY || o == Fluids.EMPTY || o == EmptyFluidIngredient.INSTANCE) {
			return FluidStack.EMPTY;
		} else if (o instanceof FluidStack stack) {
			return stack;
		} else if (o instanceof Fluid fluid) {
			return new FluidStack(fluid, FluidType.BUCKET_VOLUME);
		} else if (o instanceof FluidIngredient in) {
			return in.hasNoFluids() ? FluidStack.EMPTY : in.getStacks()[0];
		} else {
			return ofString(registries.nbt(), o.toString());
		}
	}

	static FluidIngredient wrapIngredient(RegistryAccessContainer registries, Object o) {
		if (o == null || o == FluidStack.EMPTY || o == Fluids.EMPTY || o == EmptyFluidIngredient.INSTANCE) {
			return EmptyFluidIngredient.INSTANCE;
		} else if (o instanceof FluidStack stack) {
			return FluidIngredient.of(stack);
		} else if (o instanceof Fluid fluid) {
			return FluidIngredient.of(fluid);
		} else if (o instanceof FluidIngredient in) {
			return in;
		} else {
			var stack = wrap(registries, o);
			return stack.isEmpty() ? EmptyFluidIngredient.INSTANCE : FluidIngredient.of(stack);
		}
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

	static FluidStack ofString(DynamicOps<Tag> registryOps, String s) {
		if (s.isEmpty() || s.equals("-") || s.equals("air") || s.equals("minecraft:air")) {
			return FluidStack.EMPTY;
		} else {
			try {
				var reader = new StringReader(s);
				reader.skipWhitespace();

				if (!reader.canRead()) {
					return FluidStack.EMPTY;
				}

				return read(registryOps, new StringReader(s));
			} catch (CommandSyntaxException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	static FluidStack read(DynamicOps<Tag> registryOps, StringReader reader) throws CommandSyntaxException {
		if (!reader.canRead()) {
			return FluidStack.EMPTY;
		}

		if (reader.peek() == '-') {
			return FluidStack.EMPTY;
		}

		int amount = 1;

		if (reader.canRead() && StringReader.isAllowedNumber(reader.peek())) {
			amount = Mth.ceil(reader.readDouble());
			reader.skipWhitespace();
			reader.expect('x');
			reader.skipWhitespace();

			if (amount < 1) {
				throw new IllegalArgumentException("Item count smaller than 1 is not allowed!");
			}
		}

		var fluidId = ResourceLocation.read(reader);
		var fluidStack = new FluidStack(RegistryInfo.FLUID.getValue(fluidId), amount);

		var next = reader.canRead() ? reader.peek() : 0;

		if (next == '[' || next == '{') {
			fluidStack.applyComponents(DataComponentWrapper.readPatch(registryOps, reader));
		}

		return fluidStack;
	}
}