package dev.latvian.kubejs.block;

import dev.latvian.kubejs.item.ItemJS;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

/**
 * @author LatvianModder
 */
public class BlockJS
{
	public static final BlockJS AIR = new BlockJS(Blocks.AIR)
	{
		@Override
		public boolean isAir()
		{
			return true;
		}

		@Override
		public ItemJS item()
		{
			return ItemJS.AIR;
		}

		@Override
		public BlockJS setLanguageKey(String key)
		{
			return this;
		}

		@Override
		public BlockJS setHardness(float hardness)
		{
			return this;
		}

		@Override
		public BlockJS setResistance(float resistance)
		{
			return this;
		}

		@Override
		public BlockJS setLightLevel(float light)
		{
			return this;
		}

		@Override
		public BlockJS setHarvestLevel(String tool, int level)
		{
			return this;
		}
	};

	public final Block block;

	public BlockJS(Block b)
	{
		block = b;
	}

	public boolean isAir()
	{
		return false;
	}

	public int hashCode()
	{
		return block.hashCode();
	}

	public boolean equals(Object obj)
	{
		if (obj == this || obj == block)
		{
			return true;
		}
		else if (obj instanceof BlockJS)
		{
			return block == ((BlockJS) obj).block;
		}

		return false;
	}

	public String toString()
	{
		return block.getRegistryName().toString();
	}

	public ItemJS item()
	{
		Item item = Item.getItemFromBlock(block);
		return item == Items.AIR ? ItemJS.AIR : new ItemJS(item);
	}

	public BlockJS setLanguageKey(String key)
	{
		block.setTranslationKey(key);
		return this;
	}

	public BlockJS setHardness(float hardness)
	{
		block.setHardness(hardness);
		return this;
	}

	public BlockJS setResistance(float resistance)
	{
		block.setResistance(resistance);
		return this;
	}

	public BlockJS setLightLevel(float light)
	{
		block.setLightLevel(light);
		return this;
	}

	public BlockJS setHarvestLevel(String tool, int level)
	{
		block.setHarvestLevel(tool, level);
		return this;
	}
}