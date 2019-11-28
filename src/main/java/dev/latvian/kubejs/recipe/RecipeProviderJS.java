package dev.latvian.kubejs.recipe;

import dev.latvian.kubejs.recipe.type.RecipeJS;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface RecipeProviderJS
{
	@Nullable
	RecipeJS create(Object[] args);
}