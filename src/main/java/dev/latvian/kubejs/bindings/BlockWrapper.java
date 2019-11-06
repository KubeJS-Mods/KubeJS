package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.MinecraftClass;
import dev.latvian.kubejs.block.MaterialJS;
import dev.latvian.kubejs.block.MaterialListJS;
import dev.latvian.kubejs.block.predicate.BlockEntityPredicate;
import dev.latvian.kubejs.block.predicate.BlockIDPredicate;
import dev.latvian.kubejs.block.predicate.BlockPredicate;
import dev.latvian.kubejs.documentation.DisplayName;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.documentation.T;
import dev.latvian.kubejs.util.ID;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
@DisplayName("BlockUtilities")
public class BlockWrapper
{
	public Map<String, MaterialJS> getMaterial()
	{
		return MaterialListJS.INSTANCE.map;
	}

	public BlockIDPredicate id(Object id)
	{
		return new BlockIDPredicate(id);
	}

	public BlockIDPredicate id(Object id, Map<String, Object> properties)
	{
		BlockIDPredicate b = id(id);

		for (Map.Entry<String, Object> entry : properties.entrySet())
		{
			b = b.with(entry.getKey(), entry.getValue().toString());
		}

		return b;
	}

	public BlockEntityPredicate entity(Object id)
	{
		return new BlockEntityPredicate(id);
	}

	public BlockPredicate custom(BlockPredicate predicate)
	{
		return predicate;
	}

	private Map<String, EnumFacing> facingMap;

	public Map<String, EnumFacing> getFacing()
	{
		if (facingMap == null)
		{
			facingMap = new HashMap<>(6);

			for (EnumFacing facing : EnumFacing.VALUES)
			{
				facingMap.put(facing.getName(), facing);
			}
		}

		return facingMap;
	}

	@MinecraftClass
	public Block getBlock(@P("id") @T(ID.class) Object id)
	{
		Block b = Block.REGISTRY.getObject(ID.of(id).mc());
		return b == null ? Blocks.AIR : b;
	}

	public List<ID> getTypeList()
	{
		List<ID> list = new ArrayList<>();

		for (Block block : Block.REGISTRY)
		{
			list.add(ID.of(block.getRegistryName()));
		}

		return list;
	}
}