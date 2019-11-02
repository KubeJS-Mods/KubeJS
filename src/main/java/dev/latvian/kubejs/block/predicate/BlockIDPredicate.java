package dev.latvian.kubejs.block.predicate;

import com.google.common.base.Optional;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.util.ID;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
		id = ID.of(i).mc();
	}

	@Override
	public String toString()
	{
		return id + (properties == null || properties.isEmpty() ? "" : ("+" + properties));
	}

	public BlockIDPredicate with(@P("key") String key, @P("value") String value)
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
			cachedBlock = Block.REGISTRY.getObject(id);

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

			for (IProperty<?> property : getBlock().getDefaultState().getPropertyKeys())
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

	public IBlockState getBlockState()
	{
		IBlockState state = getBlock().getDefaultState();

		for (PropertyObject object : getBlockProperties())
		{
			state = state.withProperty(object.property, UtilsJS.cast(object.value));
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

		IBlockState state = b.getBlockState();

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
			if (!state.getValue(object.property).equals(object.value))
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
			block.setHardness(hardness);
		}
	}

	public void setResistance(float resistance)
	{
		Block block = getBlock();

		if (block != Blocks.AIR)
		{
			block.setResistance(resistance);
		}
	}

	public void setLightLevel(float lightLevel)
	{
		Block block = getBlock();

		if (block != Blocks.AIR)
		{
			block.setLightLevel(lightLevel);
		}
	}

	public void setHarvestLevel(String tool, int level)
	{
		Block block = getBlock();

		if (block != Blocks.AIR)
		{
			if (properties == null || properties.isEmpty())
			{
				block.setHarvestLevel(tool, level);
			}
			else
			{
				block.setHarvestLevel(tool, level, getBlockState());
			}
		}
	}
}
