package dev.latvian.kubejs.world;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.util.Facing;
import dev.latvian.kubejs.util.ID;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.util.nbt.NBTBaseJS;
import dev.latvian.kubejs.util.nbt.NBTCompoundJS;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.effect.EntityLightningBolt;
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

	private final World world;
	private final BlockPos pos;

	public BlockContainerJS(World w, BlockPos p)
	{
		world = w;
		pos = p;
	}

	public WorldJS getWorld()
	{
		return UtilsJS.getWorld(world);
	}

	public BlockPos getPos()
	{
		return pos;
	}

	public int getX()
	{
		return getPos().getX();
	}

	public int getY()
	{
		return getPos().getY();
	}

	public int getZ()
	{
		return getPos().getZ();
	}

	public BlockContainerJS offset(Facing f, int d)
	{
		return new BlockContainerJS(world, getPos().offset(f.vanillaFacing, d));
	}

	public BlockContainerJS offset(Facing f)
	{
		return offset(f, 1);
	}

	public BlockContainerJS offset(int x, int y, int z)
	{
		return new BlockContainerJS(world, getPos().add(x, y, z));
	}

	public BlockContainerJS getDown()
	{
		return offset(Facing.DOWN);
	}

	public BlockContainerJS getUp()
	{
		return offset(Facing.UP);
	}

	public BlockContainerJS getNorth()
	{
		return offset(Facing.NORTH);
	}

	public BlockContainerJS getSouth()
	{
		return offset(Facing.SOUTH);
	}

	public BlockContainerJS getWest()
	{
		return offset(Facing.WEST);
	}

	public BlockContainerJS getEast()
	{
		return offset(Facing.EAST);
	}

	public IBlockState getBlockState()
	{
		return world.getBlockState(getPos());
	}

	public void setBlockState(IBlockState state, int flags)
	{
		world.setBlockState(getPos(), state, flags);
	}

	public ID getId()
	{
		IBlockState state = getBlockState();
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

		setBlockState(state, flags);
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
		IBlockState state = getBlockState();

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

	public void setEntityData(Object n)
	{
		NBTCompoundJS nbt = NBTBaseJS.of(n).asCompound();

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

	public boolean getCanSeeSky()
	{
		return world.canSeeSky(pos);
	}

	public boolean getCanSnow()
	{
		return world.canSnowAt(pos, false);
	}

	public boolean getCanSnowCheckingLight()
	{
		return world.canSnowAt(pos, true);
	}

	@Override
	public String toString()
	{
		ID id = getId();
		Map<String, String> properties = getProperties();
		return properties.isEmpty() ? id.toString() : (id + "+" + properties);
	}

	public ExplosionJS createExplosion()
	{
		return new ExplosionJS(world, getX() + 0.5D, getY() + 0.5D, getZ() + 0.5D);
	}

	@Nullable
	public EntityJS createEntity(Object id)
	{
		EntityJS entity = getWorld().createEntity(id);

		if (entity != null)
		{
			entity.setPosition(this);
		}

		return entity;
	}

	public void spawnLightning(boolean effectOnly)
	{
		world.addWeatherEffect(new EntityLightningBolt(world, getX(), getY(), getZ(), effectOnly));
	}

	public void spawnFireworks(FireworksJS fireworks)
	{
		world.spawnEntity(fireworks.createFireworkRocket(world, getX() + 0.5D, getY() + 0.5D, getZ() + 0.5D));
	}
}