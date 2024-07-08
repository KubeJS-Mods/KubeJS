package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.schema.RecipeComponentFactory;
import dev.latvian.mods.kubejs.registry.RegistryType;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public record RegistryComponent<T>(Registry<T> registry, @Nullable RegistryType<T> regType, Codec<T> codec) implements RecipeComponent<T> {
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static final RecipeComponentFactory FACTORY = (registries, storage, reader) -> {
		reader.skipWhitespace();
		reader.expect('<');
		reader.skipWhitespace();
		var regId = ResourceLocation.read(reader);
		reader.expect('>');
		var key = ResourceKey.createRegistryKey(regId);
		return new RegistryComponent(registries.access().registry(key).orElseThrow(), RegistryType.ofKey(key), RegistryFixedCodec.create(key));
	};

	@Override
	public Codec<T> codec() {
		return codec;
	}

	@Override
	public TypeInfo typeInfo() {
		return regType == null || regType.type() == TypeInfo.STRING ? TypeInfo.STRING : TypeInfo.STRING.or(regType.type());
	}

	@Override
	@SuppressWarnings("unchecked")
	public T wrap(Context cx, KubeRecipe recipe, Object from) {
		if (registry == BuiltInRegistries.ITEM) {
			if (from instanceof ItemStack is) {
				return (T) is.getItem();
			} else if (from instanceof Item) {
				return (T) from;
			} else {
				return (T) ItemStackJS.wrap(((KubeJSContext) cx).getRegistries(), from).getItem();
			}
		} else if (registry == BuiltInRegistries.FLUID) {
			if (from instanceof FluidStack fs) {
				return (T) fs.getFluid();
			} else if (from instanceof Fluid) {
				return (T) from;
			} else {
				return (T) FluidWrapper.wrap(((KubeJSContext) cx).getRegistries(), from).getFluid();
			}
		} else {
			if (regType != null && regType.baseClass().isInstance(from)) {
				return (T) from;
			}

			return registry.get(ID.mc(from));
		}
	}

	@Override
	public boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
		return (regType != null && regType.baseClass().isInstance(from)) || (from instanceof CharSequence && ID.mc(from.toString()) != null) || (from instanceof JsonPrimitive json && json.isString() && ID.mc(json.getAsString()) != null);
	}

	@Override
	public void buildUniqueId(UniqueIdBuilder builder, T value) {
		var id = registry.getKey(value);

		if (id != null) {
			builder.append(id);
		}
	}

	@Override
	public String toString() {
		return "registry_element<" + registry.key().location() + ">";
	}
}
