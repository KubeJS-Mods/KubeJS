package dev.latvian.kubejs.block.predicate;

import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.IProperty;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author LatvianModder
 */
public class BlockIDPredicate implements BlockPredicate
{
	private static class PropertyObject
	{
		private IProperty<?> property;
		private Object value;
	}

	private final ResourceLocation id;
	private Map<String, String> properties;
	private Block cachedBlock;
	private List<PropertyObject> cachedProperties;

	public BlockIDPredicate(Object i)
	{
		id = UtilsJS.getID(i);
	}

	@Override
	public String toString()
	{
		return id + (properties == null || properties.isEmpty() ? "" : ("+" + properties));
	}

	public BlockIDPredicate with(String key, String value)
	{
		if (properties == null)
		{
			properties = new HashMap<>();
		}

		properties.put(key, value);
		cachedBlock = null;
		cachedProperties = null;
		return this;
	}

	private Block getBlock()
	{
		if (cachedBlock == null)
		{
			cachedBlock = ForgeRegistries.BLOCKS.getValue(id);

			if (cachedBlock == null)
			{
				cachedBlock = Blocks.AIR;
			}
		}

		return cachedBlock;
	}

	public List<PropertyObject> getBlockProperties()
	{
		if (cachedProperties == null)
		{
			cachedProperties = new LinkedList<>();

			Map<String, IProperty<?>> map = new HashMap<>();

			for (IProperty<?> property : getBlock().getDefaultState().getProperties())
			{
				map.put(property.getName(), property);
			}

			for (Map.Entry<String, String> entry : properties.entrySet())
			{
				IProperty<?> property = map.get(entry.getKey());

				if (property != null)
				{
					Optional<?> o = property.parseValue(entry.getValue());

					if (o.isPresent())
					{
						PropertyObject po = new PropertyObject();
						po.property = property;
						po.value = o.get();
						cachedProperties.add(po);
					}
				}
			}
		}

		return cachedProperties;
	}

	public BlockState getBlockState()
	{
		BlockState state = getBlock().getDefaultState();

		for (PropertyObject object : getBlockProperties())
		{
			state = state.with(object.property, UtilsJS.cast(object.value));
		}

		return state;
	}

	@Override
	public boolean check(BlockContainerJS b)
	{
		if (getBlock() == Blocks.AIR)
		{
			return false;
		}

		BlockState state = b.getBlockState();

		if (state.getBlock() != getBlock())
		{
			return false;
		}

		if (properties == null || properties.isEmpty())
		{
			return true;
		}

		for (PropertyObject object : getBlockProperties())
		{
			if (!state.get(object.property).equals(object.value))
			{
				return false;
			}
		}

		return true;
	}

	public void setHardness(float hardness)
	{
		Block block = getBlock();

		if (block != Blocks.AIR)
		{
			//block.setHardness(hardness);
		}
	}

	public void setResistance(float resistance)
	{
		Block block = getBlock();

		if (block != Blocks.AIR)
		{
			//FIXME: block.setResistance(resistance);
		}
	}

	public void setLightLevel(float lightLevel)
	{
		Block block = getBlock();

		if (block != Blocks.AIR)
		{
			//FIXME: block.setLightLevel(lightLevel);
		}
	}

	public void setHarvestLevel(String tool, int level)
	{
		Block block = getBlock();

		if (block != Blocks.AIR)
		{
			if (properties == null || properties.isEmpty())
			{
				//FIXME: block.setHarvestLevel(tool, level);
			}
			else
			{
				//FIXME: block.setHarvestLevel(tool, level, getBlockState());
			}
		}
	}
}
