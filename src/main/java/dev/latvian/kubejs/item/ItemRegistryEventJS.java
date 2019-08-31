package dev.latvian.kubejs.item;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
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

	public ItemBuilder create(String name)
	{
		return new ItemBuilder(name, p -> {
			ItemJS item = new ItemJS(p);
			item.setRegistryName(UtilsJS.INSTANCE.idMC(p.id));
			registry.register(item);
		});
	}

	public ItemBuilder createBlockItem(String name)
	{
		return new ItemBuilder(name, p -> {
			Block block = Block.REGISTRY.getObject(UtilsJS.INSTANCE.idMC(p.id));

			if (block == null || block == Blocks.AIR)
			{
				throw new IllegalArgumentException("Block with name " + name + " not found!");
			}

			BlockItemJS item = new BlockItemJS(block, p);
			item.setRegistryName(block.getRegistryName());
			registry.register(item);
		});
	}
}