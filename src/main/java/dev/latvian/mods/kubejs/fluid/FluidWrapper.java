package dev.latvian.mods.kubejs.fluid;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.kubejs.component.DataComponentWrapper;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.RegExpKJS;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.crafting.DataComponentFluidIngredient;
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

	@HideFromJS
	static FluidStack wrap(Context cx, Object o) {
		var registries = RegistryAccessContainer.of(cx);

		return switch (o) {
			case null -> FluidStack.EMPTY;
			case FluidStack stack -> stack.isEmpty() ? FluidStack.EMPTY : stack;
			case Fluid fluid when fluid.kjs$isEmpty() -> FluidStack.EMPTY;
			case Fluid fluid -> new FluidStack(fluid, FluidType.BUCKET_VOLUME);
			// TODO: fail on FluidIngredient like we do for items?
			//  the big problem here is that these last three calls DISSOLVE the ingredient!
			case FluidIngredient in when in.isEmpty() || in.hasNoFluids() -> FluidStack.EMPTY;
			case FluidIngredient in -> in.getStacks()[0];
			case SizedFluidIngredient s -> s.getFluids()[0];
			default -> ofString(cx, registries.nbt(), o.toString());
		};
	}

	static FluidIngredient ingredientOf(FluidIngredient of) {
		return of;
	}

	@HideFromJS
	static FluidIngredient wrapIngredient(Context cx, Object o) {
		var registries = RegistryAccessContainer.of(cx);

		return switch (o) {
			case null -> FluidIngredient.empty();
			case FluidStack stack when stack.isEmpty() -> FluidIngredient.empty();
			case Fluid fluid when fluid.kjs$isEmpty() -> FluidIngredient.empty();
			case FluidIngredient in when in.isEmpty() -> FluidIngredient.empty();
			case FluidStack stack -> FluidIngredient.of(stack);
			case Fluid fluid -> FluidIngredient.of(fluid);
			case FluidIngredient in -> in;
			case SizedFluidIngredient s -> s.ingredient();
			default -> ingredientOfString(cx, registries.nbt(), o.toString());
		};
	}

	static SizedFluidIngredient sizedIngredientOf(SizedFluidIngredient of) {
		return of;
	}

	@HideFromJS
	static SizedFluidIngredient wrapSizedIngredient(Context cx, Object o) {
		var registries = RegistryAccessContainer.of(cx);

		return switch (o) {
			case null -> EMPTY_SIZED;
			case FluidStack stack when stack.isEmpty() -> EMPTY_SIZED;
			case Fluid fluid when fluid.kjs$isEmpty() -> EMPTY_SIZED;
			case FluidIngredient in when in.isEmpty() -> EMPTY_SIZED;
			case FluidStack stack -> SizedFluidIngredient.of(stack);
			case Fluid fluid -> SizedFluidIngredient.of(fluid, FluidType.BUCKET_VOLUME);
			case FluidIngredient in -> new SizedFluidIngredient(in, FluidType.BUCKET_VOLUME);
			case SizedFluidIngredient s -> s;
			default -> sizedIngredientOfString(cx, registries.nbt(), o.toString());
		};
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

	interface ReadFn<T> {
		T read(DynamicOps<Tag> registryOps, StringReader reader) throws CommandSyntaxException;
	}

	static <T> T readWithContext(Context cx, DynamicOps<Tag> registryOps, String s, ReadFn<T> fn) {
		try {
			var reader = new StringReader(s);
			reader.skipWhitespace();

			return fn.read(registryOps, reader);
		} catch (CommandSyntaxException e) {
			throw new KubeRuntimeException(e).source(SourceLine.of(cx));
		}
	}

	static FluidStack ofString(Context cx, DynamicOps<Tag> registryOps, String s) {
		return switch (s) {
			case "", "-", "empty", "minecraft:empty" -> FluidStack.EMPTY;
			default -> readWithContext(cx, registryOps, s, FluidWrapper::read);
		};
	}

	static FluidStack read(DynamicOps<Tag> registryOps, StringReader reader) throws CommandSyntaxException {
		if (!reader.canRead()) {
			return FluidStack.EMPTY;
		}

		if (reader.peek() == '-') {
			return FluidStack.EMPTY;
		}

		long amount = readFluidAmount(reader);
		var fluidId = ResourceLocation.read(reader);
		var fluidStack = new FluidStack(BuiltInRegistries.FLUID.get(fluidId), (int) amount);

		var next = reader.canRead() ? reader.peek() : 0;

		if (next == '[' || next == '{') {
			fluidStack.applyComponents(DataComponentWrapper.readPatch(registryOps, reader));
		}

		return fluidStack;
	}

	static FluidIngredient ingredientOfString(Context cx, DynamicOps<Tag> registryOps, String s) {
		return switch (s) {
			case "", "-", "empty", "minecraft:empty" -> FluidIngredient.empty();
			default -> readWithContext(cx, registryOps, s, FluidWrapper::readIngredient);
		};
	}

	static FluidIngredient readIngredient(DynamicOps<Tag> registryOps, StringReader reader) throws CommandSyntaxException {
		if (!reader.canRead()) {
			return FluidIngredient.empty();
		}

		switch (reader.peek()) {
			case '-' -> {
				return FluidIngredient.empty();
			}
			case '#' -> {
				reader.skip();
				var tag = ResourceLocation.read(reader);
				return FluidIngredient.tag(FluidTags.create(tag));
			}
			case '@' -> {
				reader.skip();
				var id = reader.readString();
				return new NamespaceFluidIngredient(id);
			}
			case '/' -> {
				reader.skip();
				var pattern = RegExpKJS.read(reader);
				return new RegExFluidIngredient(pattern);
			}
		}

		var fluidId = ResourceLocation.read(reader);
		var fluid = BuiltInRegistries.FLUID.get(fluidId);

		var next = reader.canRead() ? reader.peek() : 0;

		if (next == '[' || next == '{') {
			var components = DataComponentWrapper.readPredicate(registryOps, reader);

			if (components != DataComponentPredicate.EMPTY) {
				return new DataComponentFluidIngredient(HolderSet.direct(fluid.kjs$asHolder()), components, false);
			}
		}

		return FluidIngredient.of(fluid);
	}

	static SizedFluidIngredient sizedIngredientOfString(Context cx, DynamicOps<Tag> registryOps, String s) {
		return switch (s) {
			case "", "-", "empty", "minecraft:empty" -> EMPTY_SIZED;
			default -> readWithContext(cx, registryOps, s, FluidWrapper::readSizedIngredient);
		};
	}

	static SizedFluidIngredient readSizedIngredient(DynamicOps<Tag> registryOps, StringReader reader) throws CommandSyntaxException {
		if (!reader.canRead()) {
			return EMPTY_SIZED;
		}

		long amount = readFluidAmount(reader);
		return new SizedFluidIngredient(readIngredient(registryOps, reader), (int) amount);
	}

	static long readFluidAmount(StringReader reader) throws CommandSyntaxException {
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

			long amount = (long) amountd;
			reader.expect('x');
			reader.skipWhitespace();

			if (amount < 1L) {
				throw new IllegalArgumentException("Fluid amount smaller than 1 is not allowed!");
			}

			return amount;
		}

		return FluidType.BUCKET_VOLUME;
	}
}