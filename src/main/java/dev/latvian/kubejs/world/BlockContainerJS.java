package dev.latvian.kubejs.world;

import dev.latvian.kubejs.MinecraftClass;
import dev.latvian.kubejs.block.MaterialJS;
import dev.latvian.kubejs.block.MaterialListJS;
import dev.latvian.kubejs.documentation.DisplayName;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.documentation.T;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.item.InventoryJS;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
@DisplayName("Block")
public class BlockContainerJS
{
	private static final ID AIR_ID = ID.of("minecraft:air");

	public final World minecraftWorld;
	private final BlockPos pos;

	private IBlockState cachedState;
	private TileEntity cachedEntity;

	public BlockContainerJS(World w, BlockPos p)
	{
		minecraftWorld = w;
		pos = p;
	}

	public void clearCache()
	{
		cachedState = null;
		cachedEntity = null;
	}

	public WorldJS getWorld()
	{
		return UtilsJS.getWorld(minecraftWorld);
	}

	public BlockPos getPos()
	{
		return pos;
	}

	public int getDimension()
	{
		return minecraftWorld.provider.getDimension();
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

	public BlockContainerJS offset(EnumFacing f, int d)
	{
		return new BlockContainerJS(minecraftWorld, getPos().offset(f, d));
	}

	public BlockContainerJS offset(EnumFacing f)
	{
		return offset(f, 1);
	}

	public BlockContainerJS offset(int x, int y, int z)
	{
		return new BlockContainerJS(minecraftWorld, getPos().add(x, y, z));
	}

	public BlockContainerJS getDown()
	{
		return offset(EnumFacing.DOWN);
	}

	public BlockContainerJS getUp()
	{
		return offset(EnumFacing.UP);
	}

	public BlockContainerJS getNorth()
	{
		return offset(EnumFacing.NORTH);
	}

	public BlockContainerJS getSouth()
	{
		return offset(EnumFacing.SOUTH);
	}

	public BlockContainerJS getWest()
	{
		return offset(EnumFacing.WEST);
	}

	public BlockContainerJS getEast()
	{
		return offset(EnumFacing.EAST);
	}

	@MinecraftClass
	public IBlockState getBlockState()
	{
		if (cachedState == null)
		{
			cachedState = minecraftWorld.getBlockState(getPos());
		}

		return cachedState;
	}

	@MinecraftClass
	public void setBlockState(IBlockState state, int flags)
	{
		minecraftWorld.setBlockState(getPos(), state, flags);
		clearCache();
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
	@MinecraftClass
	public TileEntity getEntity()
	{
		if (cachedEntity == null || cachedEntity.isInvalid())
		{
			cachedEntity = minecraftWorld.getTileEntity(pos);
		}

		return cachedEntity;
	}

	public ID getEntityID()
	{
		TileEntity entity = getEntity();
		return entity == null ? ID.NULL_ID : ID.of(TileEntity.getKey(entity.getClass()));
	}

	public NBTCompoundJS getEntityData()
	{
		TileEntity entity = getEntity();
		return entity == null ? NBTCompoundJS.NULL : NBTBaseJS.of(entity.serializeNBT()).asCompound();
	}

	public void setEntityData(@P("nbt") @T(NBTCompoundJS.class) Object n)
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
		return minecraftWorld.getLight(pos);
	}

	public boolean getCanSeeSky()
	{
		return minecraftWorld.canSeeSky(pos);
	}

	public boolean getCanSnow()
	{
		return minecraftWorld.canSnowAt(pos, false);
	}

	public boolean getCanSnowCheckingLight()
	{
		return minecraftWorld.canSnowAt(pos, true);
	}

	@Override
	public String toString()
	{
		ID id = getId();
		Map<String, String> properties = getProperties();

		if (properties.isEmpty())
		{
			return id.toString();
		}

		StringBuilder builder = new StringBuilder(id.toString());
		builder.append('[');

		boolean first = true;

		for (Map.Entry<String, String> entry : properties.entrySet())
		{
			if (first)
			{
				first = false;
			}
			else
			{
				builder.append(',');
			}

			builder.append(entry.getKey());
			builder.append('=');
			builder.append(entry.getValue());
		}

		builder.append(']');
		return builder.toString();
	}

	public ExplosionJS createExplosion()
	{
		return new ExplosionJS(minecraftWorld, getX() + 0.5D, getY() + 0.5D, getZ() + 0.5D);
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
		minecraftWorld.addWeatherEffect(new EntityLightningBolt(minecraftWorld, getX(), getY(), getZ(), effectOnly));
	}

	public void spawnFireworks(FireworksJS fireworks)
	{
		minecraftWorld.spawnEntity(fireworks.createFireworkRocket(minecraftWorld, getX() + 0.5D, getY() + 0.5D, getZ() + 0.5D));
	}

	@Nullable
	public InventoryJS getInventory(EnumFacing facing)
	{
		TileEntity tileEntity = getEntity();

		if (tileEntity != null)
		{
			IItemHandler handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);

			if (handler != null)
			{
				return new InventoryJS(handler);
			}
		}

		return null;
	}

	public MaterialJS getMaterial()
	{
		return MaterialListJS.INSTANCE.get(getBlockState().getMaterial());
	}
}