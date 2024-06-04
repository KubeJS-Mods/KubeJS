package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.resources.ResourceLocation;

public class RecipeMappingRegistryKubeEvent implements KubeEvent {
	private final RecipeSchemaStorage storage;

	public RecipeMappingRegistryKubeEvent(RecipeSchemaStorage storage) {
		this.storage = storage;
	}

	public void mapRecipe(String name, ResourceLocation type) {
		storage.mappings.put(name, type);
	}

	@HideFromJS
	public void mapRecipe(String name, String type) {
		mapRecipe(name, new ResourceLocation(type));
	}
}