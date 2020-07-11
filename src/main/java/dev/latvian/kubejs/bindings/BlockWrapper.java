package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.block.MaterialJS;
import dev.latvian.kubejs.block.MaterialListJS;
import dev.latvian.kubejs.block.predicate.BlockEntityPredicate;
import dev.latvian.kubejs.block.predicate.BlockIDPredicate;
import dev.latvian.kubejs.block.predicate.BlockPredicate;
import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class BlockWrapper
{
	public Map<String, MaterialJS> getMaterial()
	{
		return MaterialListJS.INSTANCE.map;
	}

	public BlockIDPredicate id(@ID String id)
	{
		return new BlockIDPredicate(id);
	}

	public BlockIDPredicate id(@ID String id, Map<String, Object> properties)
	{
		BlockIDPredicate b = id(id);

		for (Map.Entry<String, Object> entry : properties.entrySet())
		{
			b = b.with(entry.getKey(), entry.getValue().toString());
		}

		return b;
	}

	public BlockEntityPredicate entity(@ID String id)
	{
		return new BlockEntityPredicate(id);
	}

	public BlockPredicate custom(BlockPredicate predicate)
	{
		return predicate;
	}

	private Map<String, Direction> facingMap;

	public Map<String, Direction> getFacing()
	{
		if (facingMap == null)
		{
			facingMap = new HashMap<>(6);

			for (Direction facing : Direction.values())
			{
				facingMap.put(facing.getString(), facing);
			}
		}

		return facingMap;
	}

	@MinecraftClass
	public Block getBlock(@ID String id)
	{
		Block b = ForgeRegistries.BLOCKS.getValue(UtilsJS.getMCID(id));
		return b == null ? Blocks.AIR : b;
	}

	public List<String> getTypeList()
	{
		List<String> list = new ArrayList<>();

		for (ResourceLocation block : ForgeRegistries.BLOCKS.getKeys())
		{
			list.add(block.toString());
		}

		return list;
	}
}