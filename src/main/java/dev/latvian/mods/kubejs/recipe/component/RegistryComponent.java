package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.schema.DynamicRecipeComponent;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.registry.RegistryType;
import dev.latvian.mods.kubejs.typings.desc.DescriptionContext;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
import dev.latvian.mods.kubejs.util.ID;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public record RegistryComponent<T>(ResourceKey<Registry<T>> registryKey) implements RecipeComponent<T> {
	@SuppressWarnings({"unchecked", "rawtypes", "DataFlowIssue"})
	public static final DynamicRecipeComponent DYNAMIC = new DynamicRecipeComponent(TypeDescJS.object(1).add("registry", TypeDescJS.STRING.or(DescriptionContext.DEFAULT.javaType(RegistryInfo.class))), (ctx, scope, args) -> {
		Object from = args.get("registry");

		return new RegistryComponent<>((ResourceKey) switch (from) {
			case RegistryType<?> registryType -> registryType.key();
			case RegistryInfo<?> registryInfo -> registryInfo.key;
			case ResourceKey<?> resourceKey -> resourceKey;
			case Registry<?> registry -> registry.key();
			case null, default -> ResourceKey.createRegistryKey(ID.mc(from));
		});
	});

	@Override
	public String componentType() {
		return "registry_element";
	}

	@Override
	public TypeDescJS constructorDescription(DescriptionContext ctx) {
		var t = RegistryType.ofKey(registryKey);
		return t == null ? TypeDescJS.STRING : TypeDescJS.STRING.or(ctx.javaType(t.baseClass()));
	}

	@Override
	public Class<?> componentClass() {
		return Registry.class;
	}

	@Override
	public JsonElement write(KubeRecipe recipe, T value) {
		var reg = RegistryInfo.of(registryKey);
		return new JsonPrimitive(reg.getId(value).toString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public T read(KubeRecipe recipe, Object from) {
		var key = (ResourceKey) registryKey;
		var regType = RegistryType.ofKey(key);

		if (regType != null && regType.baseClass().isInstance(from)) {
			return (T) from;
		} else if (!(from instanceof CharSequence) && !(from instanceof JsonPrimitive) && !(from instanceof ResourceLocation)) {
			if (key == Registries.ITEM) {
				if (from instanceof ItemStack is) {
					return (T) is.getItem();
				} else {
					return (T) recipe.readOutputItem(from).item.getItem();
				}
			} else if (key == Registries.FLUID) {
				if (from instanceof FluidStack fs) {
					return (T) fs.getFluid();
				}
			}
		}

		return RegistryInfo.of(registryKey).getValue(ID.mc(from));
	}

	@Override
	public boolean hasPriority(KubeRecipe recipe, Object from) {
		var regType = RegistryType.ofKey(registryKey);
		return (regType != null && regType.baseClass().isInstance(from)) || (from instanceof CharSequence && ID.mc(from.toString()) != null) || (from instanceof JsonPrimitive json && json.isString() && ID.mc(json.getAsString()) != null);
	}

	@Override
	public String toString() {
		return "%s{%s}".formatted(componentType(), registryKey.location());
	}
}
