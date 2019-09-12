package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocMethod;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.MatchAllIngredientJS;
import dev.latvian.kubejs.item.ingredient.MatchAnyIngredientJS;
import dev.latvian.kubejs.item.ingredient.ModIngredientJS;
import dev.latvian.kubejs.item.ingredient.OreDictionaryIngredientJS;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
@DocClass(displayName = "Ingredient Utilities")
public class IngredientWrapper
{
	@DocMethod
	public IngredientJS getNone()
	{
		return EmptyItemStackJS.INSTANCE;
	}

	@DocMethod
	public IngredientJS getAll()
	{
		return MatchAllIngredientJS.INSTANCE;
	}

	@DocMethod
	public IngredientJS of(@Nullable Object object)
	{
		return IngredientJS.of(object);
	}

	@DocMethod
	public IngredientJS custom(Predicate<ItemStackJS> predicate)
	{
		return predicate::test;
	}

	@DocMethod
	public IngredientJS matchAny(Object[] objects)
	{
		MatchAnyIngredientJS ingredient = new MatchAnyIngredientJS();
		ingredient.addAll(objects);
		return ingredient;
	}

	@DocMethod
	public IngredientJS ore(String oreName)
	{
		return new OreDictionaryIngredientJS(oreName);
	}

	@DocMethod
	public IngredientJS mod(String modId)
	{
		return new ModIngredientJS(modId);
	}
}