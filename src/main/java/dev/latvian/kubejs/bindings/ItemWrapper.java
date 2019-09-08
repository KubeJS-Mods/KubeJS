package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocMethod;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.world.FireworksJS;

import java.util.List;
import java.util.Map;

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
	public List<ItemStackJS> getList()
	{
		return ItemStackJS.getList();
	}

	@DocMethod
	public void clearListCache()
	{
		ItemStackJS.clearListCache();
	}

	public FireworksJS fireworks(Map<String, Object> properties)
	{
		return FireworksJS.of(properties);
	}
}