package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.documentation.DisplayName;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.world.FireworksJS;

import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
@DisplayName("Item Utilities")
public class ItemWrapper
{
	public ItemStackJS of(Object object)
	{
		return ItemStackJS.of(object);
	}

	public List<ItemStackJS> getList()
	{
		return ItemStackJS.getList();
	}

	public ItemStackJS getEmpty()
	{
		return EmptyItemStackJS.INSTANCE;
	}

	public void clearListCache()
	{
		ItemStackJS.clearListCache();
	}

	public FireworksJS fireworks(@P("properties") Map<String, Object> properties)
	{
		return FireworksJS.of(properties);
	}
}