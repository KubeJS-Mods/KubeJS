package dev.latvian.kubejs.recipe;

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
	private final String string;

	public RecipeTypeJS(RecipeSerializer<?> s, Supplier<RecipeJS> f) {
		serializer = s;
		factory = f;
		id = Registries.getId(s, Registry.RECIPE_SERIALIZER_REGISTRY);
		string = id.toString();
	}

	public boolean isCustom() {
		return false;
	}

	@Override
	public String toString() {
		return string;
	}

	public String getId() {
		return string;
	}

	public ResourceLocation getIdRL() {
		return id;
	}

	@Override
	public int hashCode() {
		return string.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return string.equals(obj.toString());
	}
}