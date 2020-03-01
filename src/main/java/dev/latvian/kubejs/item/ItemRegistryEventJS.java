package dev.latvian.kubejs.item;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.block.BlockJS;
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

	public ItemBuilder createBlockItem(String name)
	{
		BlockJS block = BlockJS.KUBEJS_BLOCKS.get(UtilsJS.getID(KubeJS.appendModId(name)));

		if (block == null)
		{
			throw new IllegalArgumentException("Block with name " + name + " not found!");
		}

		ItemBuilder itemBuilder = new ItemBuilder(name, p -> {
			BlockItemJS item = new BlockItemJS(block, p);
			registry.register(item.setRegistryName(item.properties.id));
			BlockItemJS.KUBEJS_BLOCK_ITEMS.put(item.properties.id, item);
		});

		itemBuilder.parentModel = block.properties.model;
		return itemBuilder;
	}
}