package dev.latvian.mods.kubejs.recipe;

import dev.architectury.registry.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class RecipeTypeJS {
	public final RecipeSerializer<?> serializer;
	public final Supplier<RecipeJS> factory;
	private final ResourceLocation id;

	public RecipeTypeJS(RecipeSerializer<?> s, Supplier<RecipeJS> f) {
		serializer = s;
		factory = f;
		id = Registries.getId(s, Registry.RECIPE_SERIALIZER_REGISTRY);
	}

	public boolean isCustom() {
		return false;
	}

	@Override
	public String toString() {
		return id.toString();
	}

	public String getMod() {
		return id.getNamespace();
	}

	public ResourceLocation getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return id.toString().equals(obj.toString());
	}
}