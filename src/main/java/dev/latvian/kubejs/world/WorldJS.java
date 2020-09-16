package dev.latvian.kubejs.world;

import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.entity.ItemEntityJS;
import dev.latvian.kubejs.entity.ItemFrameEntityJS;
import dev.latvian.kubejs.entity.LivingEntityJS;
import dev.latvian.kubejs.player.EntityArrayList;
import dev.latvian.kubejs.player.PlayerDataJS;
import dev.latvian.kubejs.player.PlayerJS;
import dev.latvian.kubejs.player.ServerPlayerJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.GameRulesJS;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.util.AttachedData;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.util.WithAttachedData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @author LatvianModder
 */
public abstract class WorldJS implements WithAttachedData
{
	@MinecraftClass
	public final World minecraftWorld;

	private AttachedData data;

	public WorldJS(World w)
	{
		minecraftWorld = w;
	}

	public abstract ScriptType getSide();

	@Override
	public AttachedData getData()
	{
		if (data == null)
		{
			data = new AttachedData(this);
		}

		return data;
	}

	public GameRulesJS getGameRules()
	{
		return new GameRulesJS(minecraftWorld.getGameRules());
	}

	@Nullable
	public ServerJS getServer()
	{
		return null;
	}

	public long getTime()
	{
		return minecraftWorld.getGameTime();
	}

	public long getLocalTime()
	{
		return minecraftWorld.getDayTime();
	}

	public String getDimension()
	{
		return minecraftWorld.getDimensionKey().getLocation().toString();
	}

	public boolean isOverworld()
	{
		return minecraftWorld.getDimensionKey() == World.OVERWORLD;
	}

	public boolean isDaytime()
	{
		return minecraftWorld.isDaytime();
	}

	public boolean isRaining()
	{
		return minecraftWorld.isRaining();
	}

	public boolean isThundering()
	{
		return minecraftWorld.isThundering();
	}

	public void setRainStrength(float strength)
	{
		minecraftWorld.setRainStrength(strength);
	}

	public BlockContainerJS getBlock(int x, int y, int z)
	{
		return getBlock(new BlockPos(x, y, z));
	}

	public BlockContainerJS getBlock(BlockPos pos)
	{
		return new BlockContainerJS(minecraftWorld, pos);
	}

	public BlockContainerJS getBlock(TileEntity blockEntity)
	{
		return getBlock(blockEntity.getPos());
	}

	public abstract PlayerDataJS getPlayerData(PlayerEntity player);

	@Nullable
	public EntityJS getEntity(@Nullable Entity e)
	{
		if (e == null)
		{
			return null;
		}
		else if (e instanceof PlayerEntity)
		{
			return getPlayerData((PlayerEntity) e).getPlayer();
		}
		else if (e instanceof LivingEntity)
		{
			return new LivingEntityJS(this, (LivingEntity) e);
		}
		else if (e instanceof ItemEntity)
		{
			return new ItemEntityJS(this, (ItemEntity) e);
		}
		else if (e instanceof ItemFrameEntity)
		{
			return new ItemFrameEntityJS(this, (ItemFrameEntity) e);
		}

		return new EntityJS(this, e);
	}

	@Nullable
	public LivingEntityJS getLivingEntity(@Nullable Entity entity)
	{
		EntityJS e = getEntity(entity);
		return e instanceof LivingEntityJS ? (LivingEntityJS) e : null;
	}

	@Nullable
	public PlayerJS getPlayer(@Nullable Entity entity)
	{
		EntityJS e = getEntity(entity);
		return e instanceof PlayerJS ? (PlayerJS) e : null;
	}

	public EntityArrayList createEntityList(Collection<? extends Entity> entities)
	{
		return new EntityArrayList(this, entities);
	}

	public EntityArrayList getPlayers()
	{
		return createEntityList(minecraftWorld.getPlayers());
	}

	public EntityArrayList getEntities()
	{
		return new EntityArrayList(this, 0);
	}

	public ExplosionJS createExplosion(double x, double y, double z)
	{
		return new ExplosionJS(minecraftWorld, x, y, z);
	}

	@Nullable
	public EntityJS createEntity(@ID String id)
	{
		EntityType<?> type = ForgeRegistries.ENTITIES.getValue(UtilsJS.getMCID(id));

		if (type == null)
		{
			return null;
		}

		return getEntity(type.create(minecraftWorld));
	}

	public void spawnLightning(double x, double y, double z, boolean effectOnly, @Nullable EntityJS player)
	{
		if (minecraftWorld instanceof ServerWorld)
		{
			LightningBoltEntity e = EntityType.LIGHTNING_BOLT.create(minecraftWorld);
			e.moveForced(x, y, z);
			e.setCaster(player instanceof ServerPlayerJS ? ((ServerPlayerJS) player).minecraftPlayer : null);
			minecraftWorld.addEntity(e);
		}
	}

	public void spawnLightning(double x, double y, double z, boolean effectOnly)
	{
		spawnLightning(x, y, z, effectOnly, null);
	}

	public void spawnFireworks(double x, double y, double z, FireworksJS f)
	{
		minecraftWorld.addEntity(f.createFireworkRocket(minecraftWorld, x, y, z));
	}
}