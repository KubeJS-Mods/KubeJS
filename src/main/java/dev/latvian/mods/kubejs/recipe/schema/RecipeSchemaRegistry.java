package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.resources.ResourceLocation;

public class RecipeSchemaRegistry implements KubeEvent {
	private final RecipeSchemaStorage storage;

	public RecipeSchemaRegistry(RecipeSchemaStorage storage) {
		this.storage = storage;
	}

	public RecipeNamespace namespace(String namespace) {
		return storage.namespace(namespace);
	}

	public void register(ResourceLocation id, RecipeSchema schema) {
		namespace(id.getNamespace()).register(id.getPath(), schema);
	}
}