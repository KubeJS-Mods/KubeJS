package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocMethod;
import dev.latvian.kubejs.item.ItemStackJS;

import java.util.List;

/**
 * @author LatvianModder
 */
@DocClass(displayName = "Item Utilities")
public class ItemWrapper
{
	@DocMethod
	public ItemStackJS of(Object object)
	{
		return ItemStackJS.of(object);
	}

	@DocMethod
	public List<ItemStackJS> list()
	{
		return ItemStackJS.list();
	}
}