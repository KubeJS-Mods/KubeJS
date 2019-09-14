package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.documentation.DisplayName;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.documentation.T;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.OreDictUtils;
import dev.latvian.kubejs.item.ingredient.IngredientJS;

import java.util.List;

/**
 * @author LatvianModder
 */
@DisplayName("Ore Dictionary Utilities")
public class OreDictWrapper
{
	public List<String> getDyes()
	{
		return OreDictUtils.DYES;
	}

	public void add(@P("json") @T(IngredientJS.class) Object ingredient, @P("json") String name)
	{
		OreDictUtils.add(IngredientJS.of(ingredient), name);
	}

	public void remove(@P("json") @T(IngredientJS.class) Object ingredient, @P("json") String name)
	{
		OreDictUtils.remove(IngredientJS.of(ingredient), name);
	}

	public List<String> getNames(@P("item") @T(ItemStackJS.class) Object item)
	{
		return OreDictUtils.getNames(ItemStackJS.of(item));
	}
}