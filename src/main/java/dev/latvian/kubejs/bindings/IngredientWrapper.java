package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocField;
import dev.latvian.kubejs.documentation.DocMethod;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.MatchAllIngredientJS;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
@DocClass(displayName = "Ingredient Utilities")
public class IngredientWrapper
{
	@DocField
	public final IngredientJS none = EmptyItemStackJS.INSTANCE;

	@DocField
	public final IngredientJS all = MatchAllIngredientJS.INSTANCE;

	@DocMethod
	public IngredientJS of(@Nullable Object object)
	{
		return IngredientJS.of(object);
	}
}