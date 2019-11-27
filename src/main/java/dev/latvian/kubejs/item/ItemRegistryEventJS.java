package dev.latvian.kubejs.item;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.block.BlockJS;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
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
			ItemBuilder.current = p;
			registry.register(new ItemJS(p).setRegistryName(p.id));
		});
	}

	public ItemBuilder createBlockItem(String name)
	{
		return new ItemBuilder(name, p -> {
			Block block = ForgeRegistries.BLOCKS.getValue(p.id);

			if (!(block instanceof BlockJS))
			{
				throw new IllegalArgumentException("Block with name " + name + " not found!");
			}

			registry.register(new BlockItemJS((BlockJS) block, p).setRegistryName(block.getRegistryName()));
		});
	}
}