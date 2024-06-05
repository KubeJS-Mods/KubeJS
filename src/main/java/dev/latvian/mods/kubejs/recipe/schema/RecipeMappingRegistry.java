package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.resources.ResourceLocation;

public class RecipeMappingRegistry implements KubeEvent {
	private final RecipeSchemaStorage storage;

	public RecipeMappingRegistry(RecipeSchemaStorage storage) {
		this.storage = storage;
	}

	public void register(String name, ResourceLocation type) {
		storage.mappings.put(name, type);
	}

	@HideFromJS
	public void register(String name, String type) {
		register(name, new ResourceLocation(type));
	}
}