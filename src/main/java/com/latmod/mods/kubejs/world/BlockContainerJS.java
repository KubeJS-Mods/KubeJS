package com.latmod.mods.kubejs.world;

import com.latmod.mods.kubejs.util.ID;
import com.latmod.mods.kubejs.util.UtilsJS;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class BlockContainerJS
{
	private static final ID AIR_ID = new ID("minecraft:air");

	private final WorldServer world;
	private final BlockPos pos;
	public final int x, y, z;

	public BlockContainerJS(WorldServer w, BlockPos p)
	{
		world = w;
		pos = p;
		x = pos.getX();
		y = pos.getY();
		z = pos.getZ();
	}

	public ID get()
	{
		IBlockState state = world.getBlockState(pos);
		return state.getBlock() == Blocks.AIR ? AIR_ID : new ID(state.getBlock().getRegistryName());
	}

	public void set(Object id, Map<?, ?> properties, int flags)
	{
		Block block = Block.getBlockFromName(UtilsJS.INSTANCE.id(id).toString());
		IBlockState state = (block == null ? Blocks.AIR : block).getDefaultState();

		if (!properties.isEmpty() && state.getBlock() != Blocks.AIR)
		{
			Map<String, IProperty> pmap = new HashMap<>();

			for (IProperty property : state.getPropertyKeys())
			{
				pmap.put(property.getName(), property);
			}

			for (Map.Entry entry : properties.entrySet())
			{
				IProperty<?> property = pmap.get(String.valueOf(entry.getKey()));

				if (property != null)
				{
					state = state.withProperty(property, UtilsJS.INSTANCE.cast(property.parseValue(String.valueOf(entry.getValue())).get()));
				}
			}
		}

		world.setBlockState(pos, state, flags);
	}

	public void set(Object id)
	{
		set(id, Collections.emptyMap(), 3);
	}

	public Map<String, String> properties()
	{
		Map<String, String> map = new HashMap<>();
		IBlockState state = world.getBlockState(pos);

		for (Map.Entry<IProperty<?>, ?> entry : state.getProperties().entrySet())
		{
			map.put(entry.getKey().getName(), entry.getKey().getName(UtilsJS.INSTANCE.cast(entry.getValue())));
		}

		return map;
	}

	@Nullable
	public TileEntity entity()
	{
		return world.getTileEntity(pos);
	}
}