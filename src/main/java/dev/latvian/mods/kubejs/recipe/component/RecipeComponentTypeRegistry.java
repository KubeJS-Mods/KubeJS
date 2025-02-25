package dev.latvian.mods.kubejs.recipe.component;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class RecipeComponentTypeRegistry {
	private final Map<ResourceLocation, RecipeComponentType<?>> map;

	public RecipeComponentTypeRegistry(Map<ResourceLocation, RecipeComponentType<?>> map) {
		this.map = map;
	}

	public void register(RecipeComponentType<?> type) {
		map.put(type.id(), type);
	}
}
