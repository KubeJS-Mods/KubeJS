package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.block.MaterialJS;
import dev.latvian.kubejs.block.MaterialListJS;
import dev.latvian.kubejs.block.predicate.BlockEntityPredicate;
import dev.latvian.kubejs.block.predicate.BlockIDPredicate;
import dev.latvian.kubejs.block.predicate.BlockPredicate;
import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.wrap.Wrap;
import me.shedaniel.architectury.registry.Registries;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

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

	public BlockIDPredicate id(@Wrap("id") String id)
	{
		return new BlockIDPredicate(id);
	}

	public BlockIDPredicate id(@Wrap("id") String id, Map<String, Object> properties)
	{
		BlockIDPredicate b = id(id);

		for (Map.Entry<String, Object> entry : properties.entrySet())
		{
			b = b.with(entry.getKey(), entry.getValue().toString());
		}

		return b;
	}

	public BlockEntityPredicate entity(@Wrap("id") String id)
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
				facingMap.put(facing.getSerializedName(), facing);
			}
		}

		return facingMap;
	}

	@MinecraftClass
	public Block getBlock(@Wrap("id") String id)
	{
		Block b = Registries.get(KubeJS.MOD_ID).get(Registry.BLOCK_REGISTRY).get(UtilsJS.getMCID(id));
		return b == null ? Blocks.AIR : b;
	}

	public List<String> getTypeList()
	{
		List<String> list = new ArrayList<>();

		for (ResourceLocation block : Registries.get(KubeJS.MOD_ID).get(Registry.BLOCK_REGISTRY).getIds())
		{
			list.add(block.toString());
		}

		return list;
	}
}