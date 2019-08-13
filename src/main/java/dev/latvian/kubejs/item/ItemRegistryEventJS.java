package dev.latvian.kubejs.item;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.util.RegistryEventJS;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * @author LatvianModder
 */
public class ItemRegistryEventJS extends RegistryEventJS<Item>
{
	ItemRegistryEventJS(IForgeRegistry<Item> r)
	{
		super(r);
	}

	public ItemJS register(String name)
	{
		Item item = setID(name, new Item());
		item.setTranslationKey(KubeJS.MOD_ID + "." + name);
		registry.register(item);
		return new ItemJS(item).setModel("kubejs:" + name + "#inventory");
	}

	public ItemJS registerBlockItem(String name)
	{
		Block block = Block.REGISTRY.getObject(new ResourceLocation(KubeJS.MOD_ID, name));

		if (block == null || block == Blocks.AIR)
		{
			throw new IllegalArgumentException("Block with name " + name + " not found!");
		}

		Item itemBlock = setID(name, new ItemBlock(block));
		block.setTranslationKey(KubeJS.MOD_ID + "." + name);
		registry.register(itemBlock);
		return new ItemJS(itemBlock).setModel("kubejs:" + name + "#inventory");
	}
}