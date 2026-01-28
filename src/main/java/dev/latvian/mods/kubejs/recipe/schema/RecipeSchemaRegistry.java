package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.resources.Identifier;

public class RecipeSchemaRegistry implements KubeEvent {
	private final RecipeSchemaStorage storage;

	public RecipeSchemaRegistry(RecipeSchemaStorage storage) {
		this.storage = storage;
	}

	public RecipeNamespace namespace(String namespace) {
		return storage.namespace(namespace);
	}

	public void register(Identifier id, RecipeSchema schema) {
		namespace(id.getNamespace()).register(id.getPath(), schema);
	}

	public void register(Identifier id, RegistryAwareSchema schema) {
		namespace(id.getNamespace()).register(id.getPath(), schema);
	}
}