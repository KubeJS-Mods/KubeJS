package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.architectury.registry.registries.Registrar;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.registry.KubeJSRegistries;
import dev.latvian.mods.kubejs.typings.desc.DescriptionContext;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;

public record RegistryComponent<T>(ResourceKey<? extends Registry<T>> registry, Class<?> registryType) implements RecipeComponent<T> {
	@Override
	public String componentType() {
		return "registry_element";
	}

	@Override
	public TypeDescJS constructorDescription(DescriptionContext ctx) {
		return TypeDescJS.STRING.or(ctx.javaType(registryType));
	}

	@Override
	public Class<?> componentClass() {
		return registryType;
	}

	private Registrar<T> reg() {
		return KubeJSRegistries.genericRegistry(UtilsJS.cast(registry));
	}

	@Override
	public JsonElement write(RecipeJS recipe, T value) {
		return new JsonPrimitive(reg().getId(value).toString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public T read(RecipeJS recipe, Object from) {
		if (registryType.isInstance(from)) {
			return (T) from;
		} else if (Registries.equals(Registry.ITEM) && from instanceof ItemStack stack) {
			return (T) stack.getItem();
		} else if (Registries.equals(Registry.FLUID) && from instanceof FluidStackJS fluid) {
			return (T) fluid.getFluid();
		}

		var s = String.valueOf(from);
		return reg().get(UtilsJS.getMCID(null, s));
	}

	@Override
	public boolean hasPriority(RecipeJS recipe, Object from) {
		return registryType.isInstance(from) || (from instanceof CharSequence && UtilsJS.getMCID(null, from.toString()) != null) || (from instanceof JsonPrimitive json && json.isString() && UtilsJS.getMCID(null, json.getAsString()) != null);
	}

	@Override
	public String toString() {
		return "%s{%s}".formatted(componentType(), registry.location());
	}
}
