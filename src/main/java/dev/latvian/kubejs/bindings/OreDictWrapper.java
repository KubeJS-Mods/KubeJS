package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocField;
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
	@DocField
	public final List<String> dyes = OreDictUtils.DYES;

	@DocMethod(params = {@Param("name"), @Param(value = "item", type = ItemStackJS.class)})
	public void registerOre(String name, Object ingredient)
	{
		OreDictUtils.registerOre(name, IngredientJS.of(ingredient));
	}

	@DocMethod(params = @Param(value = "item", type = ItemStackJS.class))
	public List<String> names(Object item)
	{
		return OreDictUtils.names(ItemStackJS.of(item));
	}

	@DocMethod
	public List<ItemStackJS> items(String ore)
	{
		return OreDictUtils.items(ore);
	}
}