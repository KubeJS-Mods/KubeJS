package dev.latvian.kubejs.item;

import dev.latvian.kubejs.block.BlockJS;
import dev.latvian.kubejs.util.ID;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class ItemJS
{
	public static ItemJS AIR = new ItemJS(Items.AIR)
	{
		@Override
		public boolean isAir()
		{
			return false;
		}

		@Override
		public ID id()
		{
			return new ID(item.getRegistryName());
		}

		@Override
		public BlockJS block()
		{
			Block block = Block.getBlockFromItem(item);
			return block == Blocks.AIR ? BlockJS.AIR : new BlockJS(block);
		}

		@Override
		public ItemJS setLanguageKey(String key)
		{
			return this;
		}

		@Override
		public ItemJS setMaxStackSize(int max)
		{
			return this;
		}

		@Override
		public ItemJS setMaxDamage(int max)
		{
			return this;
		}

		@Override
		public ItemJS setContainerItem(ItemJS i)
		{
			return this;
		}

		@Override
		public ItemJS setHarvestLevel(String tool, int level)
		{
			return this;
		}

		@Override
		public ItemJS setModel(String model)
		{
			return this;
		}
	};

	public static final Map<Item, String> ITEM_MODELS = new HashMap<>();

	public final Item item;

	public ItemJS(Item i)
	{
		item = i;
	}

	public boolean isAir()
	{
		return false;
	}

	public ID id()
	{
		return new ID(item.getRegistryName());
	}

	public BlockJS block()
	{
		Block block = Block.getBlockFromItem(item);
		return block == Blocks.AIR ? BlockJS.AIR : new BlockJS(block);
	}

	public int hashCode()
	{
		return item.hashCode();
	}

	public boolean equals(Object obj)
	{
		if (obj == this || obj == item)
		{
			return true;
		}
		else if (obj instanceof ItemJS)
		{
			return item == ((ItemJS) obj).item;
		}

		return false;
	}

	public String toString()
	{
		return item.getRegistryName().toString();
	}

	public ItemJS setLanguageKey(String key)
	{
		item.setTranslationKey(key);
		return this;
	}

	public ItemJS setMaxStackSize(int max)
	{
		item.setMaxStackSize(max);
		return this;
	}

	public ItemJS setMaxDamage(int max)
	{
		item.setMaxDamage(max);
		return this;
	}

	public ItemJS setContainerItem(ItemJS i)
	{
		item.setContainerItem(i.item);
		return this;
	}

	public ItemJS setHarvestLevel(String tool, int level)
	{
		item.setHarvestLevel(tool, level);
		return this;
	}

	public ItemJS setModel(String model)
	{
		ITEM_MODELS.put(item, model);
		return this;
	}
}