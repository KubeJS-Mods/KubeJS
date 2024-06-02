package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.resources.ResourceLocation;

public class RecipeSchemaRegistryKubeEvent implements KubeEvent {
	private final RecipeSchemaStorage storage;

	public RecipeSchemaRegistryKubeEvent(RecipeSchemaStorage storage) {
		this.storage = storage;
	}

	public RecipeNamespace namespace(String namespace) {
		return storage.namespaces.computeIfAbsent(namespace, RecipeNamespace::new);
	}

	public void register(ResourceLocation id, RecipeSchema schema) {
		namespace(id.getNamespace()).register(id.getPath(), schema);
	}

	public void mapRecipe(String name, ResourceLocation type) {
		storage.mappings.put(name, type);
	}

	public void mapRecipe(String name, String type) {
		mapRecipe(name, new ResourceLocation(type));
	}
}