package dev.latvian.kubejs.recipe;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.recipe.type.RecipeJS;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface RecipeDeserializerJS
{
	@Nullable
	RecipeJS create(JsonObject json);
}