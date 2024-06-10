package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.schema.RecipeComponentFactory;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.registry.RegistryType;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public record RegistryComponent<T>(RegistryInfo<T> registry) implements RecipeComponent<T> {
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static final RecipeComponentFactory FACTORY = (storage, reader) -> {
		reader.skipWhitespace();
		reader.expect('<');
		reader.skipWhitespace();
		var regId = ResourceLocation.read(reader);
		reader.expect('>');
		return new RegistryComponent(RegistryInfo.of(ResourceKey.createRegistryKey(regId)));
	};

	@Override
	public Codec<T> codec() {
		return registry.valueByNameCodec();
	}

	@Override
	public TypeInfo typeInfo() {
		var t = RegistryType.ofKey(registry.key);
		return t == null || t.type() == TypeInfo.STRING ? TypeInfo.STRING : TypeInfo.STRING.or(t.type());
	}

	@SuppressWarnings("unchecked")
	@Override
	public T wrap(Context cx, KubeRecipe recipe, Object from) {
		if (registry == RegistryInfo.ITEM) {
			if (from instanceof ItemStack is) {
				return (T) is.getItem();
			} else if (from instanceof Item) {
				return (T) from;
			} else {
				return (T) ItemStackJS.of(from).getItem();
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
	public boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
		var regType = RegistryType.ofKey(registry.key);
		return (regType != null && regType.baseClass().isInstance(from)) || (from instanceof CharSequence && ID.mc(from.toString()) != null) || (from instanceof JsonPrimitive json && json.isString() && ID.mc(json.getAsString()) != null);
	}

	@Override
	public String toString() {
		return "registry_element<" + registry + ">";
	}
}
