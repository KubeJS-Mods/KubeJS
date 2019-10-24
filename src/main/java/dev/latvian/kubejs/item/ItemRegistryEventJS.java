package dev.latvian.kubejs.item;

import dev.latvian.kubejs.block.BlockJS;
import dev.latvian.kubejs.event.EventJS;
import net.minecraft.block.Block;
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
			ItemBuilder.current = p;
			registry.register(new ItemJS().setRegistryName(p.id.mc()));
		});
	}

	public ItemBuilder createBlockItem(String name)
	{
		return new ItemBuilder(name, p -> {
			Block block = Block.REGISTRY.getObject(p.id.mc());

			if (!(block instanceof BlockJS))
			{
				throw new IllegalArgumentException("Block with name " + name + " not found!");
			}

			registry.register(new BlockItemJS((BlockJS) block, p).setRegistryName(block.getRegistryName()));
		});
	}
}