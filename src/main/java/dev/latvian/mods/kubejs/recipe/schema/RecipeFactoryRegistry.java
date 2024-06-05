package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class RecipeFactoryRegistry {
	private final RecipeSchemaStorage storage;

	public RecipeFactoryRegistry(RecipeSchemaStorage storage) {
		this.storage = storage;
	}

	public void register(KubeRecipeFactory type) {
		storage.recipeTypes.put(type.id(), type);
	}

	public void register(ResourceLocation id, Class<?> typeClass, Supplier<? extends KubeRecipe> factory) {
		register(new KubeRecipeFactory(id, typeClass, factory));
	}
}