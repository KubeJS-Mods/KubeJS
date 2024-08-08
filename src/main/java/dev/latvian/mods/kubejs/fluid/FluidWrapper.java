package dev.latvian.mods.kubejs.fluid;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.kubejs.component.DataComponentWrapper;
import dev.latvian.mods.kubejs.util.RegExpKJS;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.crafting.DataComponentFluidIngredient;
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

	SizedFluidIngredient EMPTY_SIZED = new SizedFluidIngredient(FluidIngredient.empty(), FluidType.BUCKET_VOLUME);

	static FluidStack wrap(RegistryAccessContainer registries, Object o) {
		if (o == null || o == FluidStack.EMPTY || o == Fluids.EMPTY || o == EmptyFluidIngredient.INSTANCE) {
			return FluidStack.EMPTY;
		} else if (o instanceof FluidStack stack) {
			return stack;
		} else if (o instanceof Fluid fluid) {
			return new FluidStack(fluid, FluidType.BUCKET_VOLUME);
		} else if (o instanceof FluidIngredient in) {
			return in.hasNoFluids() ? FluidStack.EMPTY : in.getStacks()[0];
		} else if (o instanceof SizedFluidIngredient s) {
			return s.getFluids()[0];
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
		} else if (o instanceof SizedFluidIngredient s) {
			return s.ingredient();
		} else {
			return ingredientOfString(registries.nbt(), o.toString());
		}
	}

	static SizedFluidIngredient wrapSizedIngredient(RegistryAccessContainer registries, Object o) {
		if (o == null || o == FluidStack.EMPTY || o == Fluids.EMPTY || o == EmptyFluidIngredient.INSTANCE) {
			return EMPTY_SIZED;
		} else if (o instanceof SizedFluidIngredient s) {
			return s;
		} else if (o instanceof FluidStack stack) {
			return SizedFluidIngredient.of(stack);
		} else if (o instanceof Fluid fluid) {
			return SizedFluidIngredient.of(fluid, FluidType.BUCKET_VOLUME);
		} else if (o instanceof FluidIngredient in) {
			return new SizedFluidIngredient(in, FluidType.BUCKET_VOLUME);
		} else {
			return sizedIngredientOfString(registries.nbt(), o.toString());
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
		return BuiltInRegistries.FLUID.get(id);
	}

	static List<String> getTypes() {
		var types = new ArrayList<String>();

		for (var fluid : BuiltInRegistries.FLUID) {
			types.add(fluid.kjs$getId());
		}

		return types;
	}

	static FluidStack getEmpty() {
		return FluidStack.EMPTY;
	}

	static boolean exists(ResourceLocation id) {
		return BuiltInRegistries.FLUID.containsKey(id);
	}

	static ResourceLocation getId(Fluid fluid) {
		return BuiltInRegistries.FLUID.getKey(fluid);
	}

	static FluidStack ofString(DynamicOps<Tag> registryOps, String s) {
		if (s.isEmpty() || s.equals("-") || s.equals("empty") || s.equals("minecraft:empty")) {
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

		int amount = readFluidAmount(reader);
		var fluidId = ResourceLocation.read(reader);
		var fluidStack = new FluidStack(BuiltInRegistries.FLUID.get(fluidId), amount);

		var next = reader.canRead() ? reader.peek() : 0;

		if (next == '[' || next == '{') {
			fluidStack.applyComponents(DataComponentWrapper.readPatch(registryOps, reader));
		}

		return fluidStack;
	}

	static FluidIngredient ingredientOfString(DynamicOps<Tag> registryOps, String s) {
		if (s.isEmpty() || s.equals("-") || s.equals("empty") || s.equals("minecraft:empty")) {
			return FluidIngredient.empty();
		} else {
			try {
				var reader = new StringReader(s);
				reader.skipWhitespace();

				if (!reader.canRead()) {
					return FluidIngredient.empty();
				}

				return readIngredient(registryOps, new StringReader(s));
			} catch (CommandSyntaxException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	static FluidIngredient readIngredient(DynamicOps<Tag> registryOps, StringReader reader) throws CommandSyntaxException {
		if (!reader.canRead()) {
			return FluidIngredient.empty();
		}

		if (reader.peek() == '-') {
			return FluidIngredient.empty();
		} else if (reader.peek() == '#') {
			reader.skip();
			var tag = ResourceLocation.read(reader);
			return FluidIngredient.tag(FluidTags.create(tag));
		} else if (reader.peek() == '@') {
			reader.skip();
			var id = reader.readString();
			return new NamespaceFluidIngredient(id);
		} else if (reader.peek() == '/') {
			reader.skip();
			var pattern = RegExpKJS.read(reader);
			return new RegExFluidIngredient(pattern);
		}

		var fluidId = ResourceLocation.read(reader);
		var fluid = BuiltInRegistries.FLUID.get(fluidId);

		var next = reader.canRead() ? reader.peek() : 0;

		if (next == '[' || next == '{') {
			var components = DataComponentWrapper.readPredicate(registryOps, reader);

			if (components != DataComponentPredicate.EMPTY) {
				return new DataComponentFluidIngredient(HolderSet.direct(fluid.builtInRegistryHolder()), components, false);
			}
		}

		return FluidIngredient.of(fluid);
	}

	static SizedFluidIngredient sizedIngredientOfString(DynamicOps<Tag> registryOps, String s) {
		if (s.isEmpty() || s.equals("-") || s.equals("empty") || s.equals("minecraft:empty")) {
			return EMPTY_SIZED;
		} else {
			try {
				var reader = new StringReader(s);
				reader.skipWhitespace();

				if (!reader.canRead()) {
					return EMPTY_SIZED;
				}

				return readSizedIngredient(registryOps, new StringReader(s));
			} catch (CommandSyntaxException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	static SizedFluidIngredient readSizedIngredient(DynamicOps<Tag> registryOps, StringReader reader) throws CommandSyntaxException {
		if (!reader.canRead()) {
			return EMPTY_SIZED;
		}

		int amount = readFluidAmount(reader);
		return new SizedFluidIngredient(readIngredient(registryOps, reader), amount);
	}

	static int readFluidAmount(StringReader reader) throws CommandSyntaxException {
		if (reader.canRead() && StringReader.isAllowedNumber(reader.peek())) {
			var amountd = reader.readDouble();
			reader.skipWhitespace();

			if (reader.peek() == 'b' || reader.peek() == 'B') {
				reader.skip();
				reader.skipWhitespace();
				amountd *= FluidType.BUCKET_VOLUME;
			}

			if (reader.peek() == '/') {
				reader.skip();
				reader.skipWhitespace();
				amountd = amountd / reader.readDouble();
			}

			int amount = Mth.ceil(amountd);
			reader.expect('x');
			reader.skipWhitespace();

			if (amount < 1) {
				throw new IllegalArgumentException("Fluid amount smaller than 1 is not allowed!");
			}

			return amount;
		}

		return FluidType.BUCKET_VOLUME;
	}
}