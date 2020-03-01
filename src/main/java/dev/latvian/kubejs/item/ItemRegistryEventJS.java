package dev.latvian.kubejs.item;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * @author LatvianModder
 */
public class ItemRegistryEventJS extends EventJS
{
	private final IForgeRegistry<Item> registry;

	ItemRegistryEventJS(IForgeRegistry<Item> r)
	{
		registry = r;
	}

	public void register(String id, Item item)
	{
		registry.register(item.setRegistryName(UtilsJS.getID(KubeJS.appendModId(id))));
	}

	public ItemBuilder create(String name)
	{
		return new ItemBuilder(name, p -> {
			ItemJS item = new ItemJS(p);
			registry.register(item.setRegistryName(p.id));
			ItemJS.KUBEJS_ITEMS.put(p.id, item);
		});
	}

	@Deprecated
	public ItemBuilder createBlockItem(String name)
	{
		return new ItemBuilder(name, p -> KubeJS.LOGGER.error("This method is deprecated! Replaced by block registry .item(function(item) { /*chained item functions here*/ }) or .noItem()"));
	}
}