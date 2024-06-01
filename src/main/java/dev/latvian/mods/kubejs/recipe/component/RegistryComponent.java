package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.schema.DynamicRecipeComponent;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.registry.RegistryType;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.rhino.type.JSObjectTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public record RegistryComponent<T>(RegistryInfo<T> registry) implements RecipeComponent<T> {
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static final DynamicRecipeComponent DYNAMIC = new DynamicRecipeComponent(JSObjectTypeInfo.of(
		new JSObjectTypeInfo.Field("registry", TypeInfo.of(RegistryInfo.class))
	), (ctx, scope, args) -> new RegistryComponent<>((RegistryInfo) ctx.jsToJava(args.get("registry"), TypeInfo.of(RegistryInfo.class))));

	@Override
	public String componentType() {
		return "registry_element";
	}

	@Override
	public TypeInfo typeInfo() {
		var t = RegistryType.ofKey(registry.key);
		return t == null || t.type() == TypeInfo.STRING ? TypeInfo.STRING : TypeInfo.STRING.or(t.type());
	}

	@Override
	public JsonElement write(KubeRecipe recipe, T value) {
		var reg = RegistryInfo.of(registry.key);
		return new JsonPrimitive(reg.getId(value).toString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public T read(KubeRecipe recipe, Object from) {
		if (registry == RegistryInfo.ITEM) {
			if (from instanceof ItemStack is) {
				return (T) is.getItem();
			} else if (from instanceof Item) {
				return (T) from;
			} else {
				return (T) recipe.readOutputItem(from).item.getItem();
			}
		} else if (registry == RegistryInfo.FLUID) {
			if (from instanceof FluidStack fs) {
				return (T) fs.getFluid();
			} else if (from instanceof Fluid) {
				return (T) from;
			} else {
				return (T) FluidWrapper.wrap(from).getFluid();
			}
		} else {
			var regType = RegistryType.ofKey(registry.key);

			if (regType != null && regType.baseClass().isInstance(from)) {
				return (T) from;
			}

			return registry.getValue(ID.mc(from));
		}
	}

	@Override
	public boolean hasPriority(KubeRecipe recipe, Object from) {
		var regType = RegistryType.ofKey(registry.key);
		return (regType != null && regType.baseClass().isInstance(from)) || (from instanceof CharSequence && ID.mc(from.toString()) != null) || (from instanceof JsonPrimitive json && json.isString() && ID.mc(json.getAsString()) != null);
	}

	@Override
	public String toString() {
		return "%s{%s}".formatted(componentType(), registry);
	}
}
