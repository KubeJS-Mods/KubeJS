package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocMethod;
import dev.latvian.kubejs.documentation.Param;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.OreDictUtils;
import dev.latvian.kubejs.item.ingredient.IngredientJS;

import java.util.List;

/**
 * @author LatvianModder
 */
@DocClass
public class OreDictWrapper
{
	@DocMethod
	public List<String> getDyes()
	{
		return OreDictUtils.DYES;
	}

	@DocMethod(params = {@Param(value = "ingredient", type = IngredientJS.class), @Param("name")})
	public void add(Object ingredient, String name)
	{
		OreDictUtils.add(IngredientJS.of(ingredient), name);
	}

	@DocMethod(params = {@Param(value = "ingredient", type = IngredientJS.class), @Param("name")})
	public void remove(Object ingredient, String name)
	{
		OreDictUtils.remove(IngredientJS.of(ingredient), name);
	}

	@DocMethod(params = @Param(value = "item", type = ItemStackJS.class))
	public List<String> getNames(Object item)
	{
		return OreDictUtils.getNames(ItemStackJS.of(item));
	}
}