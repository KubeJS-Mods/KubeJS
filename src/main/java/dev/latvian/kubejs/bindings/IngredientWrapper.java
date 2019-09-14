package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.documentation.DisplayName;
import dev.latvian.kubejs.documentation.Info;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.MatchAllIngredientJS;
import dev.latvian.kubejs.item.ingredient.MatchAnyIngredientJS;
import dev.latvian.kubejs.item.ingredient.ModIngredientJS;
import dev.latvian.kubejs.item.ingredient.OreDictionaryIngredientJS;

import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
@DisplayName("Ingredient Utilities")
public class IngredientWrapper
{
	@Info("Return ingredient that doesn't match any item")
	public IngredientJS getNone()
	{
		return EmptyItemStackJS.INSTANCE;
	}

	@Info("Return ingredient that matches any item")
	public IngredientJS getAll()
	{
		return MatchAllIngredientJS.INSTANCE;
	}

	@Info("Returns ingredient from input")
	public IngredientJS of(@P("object") Object object)
	{
		return IngredientJS.of(object);
	}

	@Info("Returns a custom ingredient using function(item){return [true/false based on item];}")
	public IngredientJS custom(@P("predicate") Predicate<ItemStackJS> predicate)
	{
		return predicate::test;
	}

	@Info("Returns ingredient that matches any of other ingredients")
	public IngredientJS matchAny(Object[] objects)
	{
		MatchAnyIngredientJS ingredient = new MatchAnyIngredientJS();
		ingredient.addAll(objects);
		return ingredient;
	}

	@Info("Returns Ore Dictionary ingredient")
	public IngredientJS ore(@P("oreName") String oreName)
	{
		return new OreDictionaryIngredientJS(oreName);
	}

	@Info("Returns mod ingredient, matches all items from mod ID")
	public IngredientJS mod(@P("modID") String modId)
	{
		return new ModIngredientJS(modId);
	}
}