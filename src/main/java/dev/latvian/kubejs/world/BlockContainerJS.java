package dev.latvian.kubejs.world;

import dev.latvian.kubejs.util.Facing;
import dev.latvian.kubejs.util.ID;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.util.nbt.NBTBaseJS;
import dev.latvian.kubejs.util.nbt.NBTCompoundJS;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class BlockContainerJS
{
	private static final ID AIR_ID = ID.of("minecraft:air");

	public final transient World world;
	public final transient BlockPos pos;
	public final int x, y, z;

	public BlockContainerJS(World w, BlockPos p)
	{
		world = w;
		pos = p;
		x = pos.getX();
		y = pos.getY();
		z = pos.getZ();
	}

	public BlockContainerJS offset(Facing f, int d)
	{
		return new BlockContainerJS(world, pos.offset(f.vanillaFacing, d));
	}

	public BlockContainerJS offset(Facing f)
	{
		return offset(f, 1);
	}

	public ID get()
	{
		IBlockState state = world.getBlockState(pos);
		return state.getBlock() == Blocks.AIR ? AIR_ID : ID.of(state.getBlock().getRegistryName());
	}

	public void set(Object id, Map<?, ?> properties, int flags)
	{
		Block block = id instanceof Block ? (Block) id : Block.getBlockFromName(ID.of(id).toString());
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
					state = state.withProperty(property, UtilsJS.cast(property.parseValue(String.valueOf(entry.getValue())).get()));
				}
			}
		}

		world.setBlockState(pos, state, flags);
	}

	public void set(Object id, Map<?, ?> properties)
	{
		set(id, properties, 3);
	}

	public void set(Object id)
	{
		set(id, Collections.emptyMap());
	}

	public Map<String, String> getProperties()
	{
		Map<String, String> map = new HashMap<>();
		IBlockState state = world.getBlockState(pos);

		for (Map.Entry<IProperty<?>, ?> entry : state.getProperties().entrySet())
		{
			map.put(entry.getKey().getName(), entry.getKey().getName(UtilsJS.cast(entry.getValue())));
		}

		return map;
	}

	@Nullable
	public TileEntity getEntity()
	{
		return world.getTileEntity(pos);
	}

	public NBTCompoundJS getEntityData()
	{
		TileEntity entity = getEntity();
		return entity == null ? NBTCompoundJS.NULL : NBTBaseJS.of(entity.serializeNBT()).asCompound();
	}

	public void setEntityData(NBTCompoundJS nbt)
	{
		if (!nbt.isNull())
		{
			TileEntity entity = getEntity();

			if (entity != null)
			{
				entity.deserializeNBT(nbt.createNBT());
			}
		}
	}

	public int getLight()
	{
		return world.getLight(pos);
	}

	public boolean canSeeSky()
	{
		return world.canSeeSky(pos);
	}

	public boolean canSnow(boolean checkLight)
	{
		return world.canSnowAt(pos, checkLight);
	}

	@Override
	public String toString()
	{
		ID id = get();
		Map<String, String> properties = getProperties();
		return properties.isEmpty() ? id.toString() : (id + "+" + properties);
	}
}