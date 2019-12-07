package dev.latvian.kubejs.world;

import dev.latvian.kubejs.MinecraftClass;
import dev.latvian.kubejs.block.MaterialJS;
import dev.latvian.kubejs.block.MaterialListJS;
import dev.latvian.kubejs.documentation.DisplayName;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.item.InventoryJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.state.IProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

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
	private static final ResourceLocation AIR_ID = new ResourceLocation("minecraft:air");

	public final IWorld minecraftWorld;
	private final BlockPos pos;

	private BlockState cachedState;
	private TileEntity cachedEntity;

	public BlockContainerJS(IWorld w, BlockPos p)
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
		return minecraftWorld.getDimension().getType().getId();
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

	public BlockContainerJS offset(Direction f, int d)
	{
		return new BlockContainerJS(minecraftWorld, getPos().offset(f, d));
	}

	public BlockContainerJS offset(Direction f)
	{
		return offset(f, 1);
	}

	public BlockContainerJS offset(int x, int y, int z)
	{
		return new BlockContainerJS(minecraftWorld, getPos().add(x, y, z));
	}

	public BlockContainerJS getDown()
	{
		return offset(Direction.DOWN);
	}

	public BlockContainerJS getUp()
	{
		return offset(Direction.UP);
	}

	public BlockContainerJS getNorth()
	{
		return offset(Direction.NORTH);
	}

	public BlockContainerJS getSouth()
	{
		return offset(Direction.SOUTH);
	}

	public BlockContainerJS getWest()
	{
		return offset(Direction.WEST);
	}

	public BlockContainerJS getEast()
	{
		return offset(Direction.EAST);
	}

	@MinecraftClass
	public BlockState getBlockState()
	{
		if (cachedState == null)
		{
			cachedState = minecraftWorld.getBlockState(getPos());
		}

		return cachedState;
	}

	@MinecraftClass
	public void setBlockState(BlockState state, int flags)
	{
		minecraftWorld.setBlockState(getPos(), state, flags);
		clearCache();
	}

	public ResourceLocation getId()
	{
		return getBlockState().getBlock().getRegistryName();
	}

	public void set(Object id, Map<?, ?> properties, int flags)
	{
		Block block = id instanceof Block ? (Block) id : ForgeRegistries.BLOCKS.getValue(UtilsJS.getID(id));
		BlockState state = (block == null ? Blocks.AIR : block).getDefaultState();

		if (!properties.isEmpty() && state.getBlock() != Blocks.AIR)
		{
			Map<String, IProperty> pmap = new HashMap<>();

			for (IProperty property : state.getProperties())
			{
				pmap.put(property.getName(), property);
			}

			for (Map.Entry entry : properties.entrySet())
			{
				IProperty<?> property = pmap.get(String.valueOf(entry.getKey()));

				if (property != null)
				{
					state = state.with(property, UtilsJS.cast(property.parseValue(String.valueOf(entry.getValue())).get()));
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
		BlockState state = getBlockState();

		for (IProperty property : state.getProperties())
		{
			map.put(property.getName(), property.getName(state.get(property)));
		}

		return map;
	}

	@Nullable
	@MinecraftClass
	public TileEntity getEntity()
	{
		if (cachedEntity == null || cachedEntity.isRemoved())
		{
			cachedEntity = minecraftWorld.getTileEntity(pos);
		}

		return cachedEntity;
	}

	public ResourceLocation getEntityID()
	{
		TileEntity entity = getEntity();
		return entity == null ? UtilsJS.NULL_ID : entity.getType().getRegistryName();
	}

	@Nullable
	public MapJS getEntityData()
	{
		final TileEntity entity = getEntity();

		if (entity != null)
		{
			MapJS entityData = MapJS.of(entity.serializeNBT());

			if (entityData != null)
			{
				entityData.changeListener = o -> entity.deserializeNBT(MapJS.nbt(o));
				return entityData;
			}
		}

		return null;
	}

	public int getLight()
	{
		return minecraftWorld.getLight(pos);
	}

	public boolean getCanSeeSky()
	{
		return minecraftWorld.canBlockSeeSky(pos);
	}

	@Override
	public String toString()
	{
		ResourceLocation id = getId();
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
		if (minecraftWorld instanceof ServerWorld)
		{
			((ServerWorld) minecraftWorld).addLightningBolt(new LightningBoltEntity((ServerWorld) minecraftWorld, getX(), getY(), getZ(), effectOnly));
		}
	}

	public void spawnFireworks(FireworksJS fireworks)
	{
		if (minecraftWorld instanceof World)
		{
			minecraftWorld.addEntity(fireworks.createFireworkRocket((World) minecraftWorld, getX() + 0.5D, getY() + 0.5D, getZ() + 0.5D));
		}
	}

	@Nullable
	public InventoryJS getInventory(Direction facing)
	{
		TileEntity tileEntity = getEntity();

		if (tileEntity != null)
		{
			IItemHandler handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing).orElse(null);

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

	@SuppressWarnings("deprecation")
	public ItemStackJS getItem()
	{
		BlockState state = getBlockState();
		return ItemStackJS.of(state.getBlock().getItem(minecraftWorld, pos, state));
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof CharSequence || obj instanceof ResourceLocation)
		{
			return getId().equals(UtilsJS.getID(obj));
		}

		return super.equals(obj);
	}
}