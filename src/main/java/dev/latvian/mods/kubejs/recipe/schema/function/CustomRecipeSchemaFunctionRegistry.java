package dev.latvian.mods.kubejs.recipe.schema.function;

import net.minecraft.resources.ResourceLocation;

@FunctionalInterface
public interface CustomRecipeSchemaFunctionRegistry {
	void register(ResourceLocation id, ResolvedRecipeSchemaFunction function);
}
