package dev.latvian.kubejs.world;

import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocField;
import dev.latvian.kubejs.documentation.DocMethod;
import dev.latvian.kubejs.documentation.Param;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.entity.LivingEntityJS;
import dev.latvian.kubejs.player.EntityArrayList;
import dev.latvian.kubejs.player.PlayerDataJS;
import dev.latvian.kubejs.player.PlayerJS;
import net.minecraft.command.CommandException;
import net.minecraft.command.EntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author LatvianModder
 */
@DocClass("This class represents a dimension. You can access weather, blocks, entities, etc. Client and server sides have different worlds")
public class WorldJS
{
	public final transient World world;

	@DocField
	public final int dimension;

	@DocField("Temporary data, mods can attach objects to this")
	public final Map<String, Object> data;

	public WorldJS(World w)
	{
		world = w;
		dimension = world.provider.getDimension();
		data = new HashMap<>();
	}

	@DocMethod
	public boolean isServer()
	{
		return !world.isRemote;
	}

	@DocMethod
	public long getSeed()
	{
		return world.getSeed();
	}

	@DocMethod
	public long getTime()
	{
		return world.getTotalWorldTime();
	}

	@DocMethod
	public long getLocalTime()
	{
		return world.getWorldTime();
	}

	@DocMethod
	public void setTime(long time)
	{
		world.setTotalWorldTime(time);
	}

	@DocMethod
	public void setLocalTime(long time)
	{
		world.setWorldTime(time);
	}

	@DocMethod
	public boolean isOverworld()
	{
		return dimension == 0;
	}

	@DocMethod
	public boolean isDaytime()
	{
		return world.isDaytime();
	}

	@DocMethod
	public boolean isRaining()
	{
		return world.isRaining();
	}

	@DocMethod(params = @Param("strength"))
	public void setRainStrength(float strength)
	{
		world.setRainStrength(strength);
	}

	@DocMethod(params = {@Param("x"), @Param("y"), @Param("z")})
	public BlockContainerJS getBlock(int x, int y, int z)
	{
		return getBlock(new BlockPos(x, y, z));
	}

	@DocMethod(params = @Param("pos"))
	public BlockContainerJS getBlock(BlockPos pos)
	{
		return new BlockContainerJS(world, pos);
	}

	@Nullable
	public PlayerDataJS getPlayerData(UUID id)
	{
		return null;
	}

	@Nullable
	@DocMethod
	public EntityJS getEntity(@Nullable Entity entity)
	{
		if (entity == null)
		{
			return null;
		}
		else if (entity instanceof EntityPlayer)
		{
			PlayerDataJS data = getPlayerData(entity.getUniqueID());

			if (data == null)
			{
				throw new NullPointerException("Player from UUID " + entity.getUniqueID() + " not found!");
			}

			return data.getPlayer();
		}
		else if (entity instanceof EntityLivingBase)
		{
			return new LivingEntityJS(this, (EntityLivingBase) entity);
		}

		return new EntityJS(this, entity);
	}

	@Nullable
	@DocMethod
	public LivingEntityJS getLivingEntity(@Nullable Entity entity)
	{
		EntityJS e = getEntity(entity);
		return e instanceof LivingEntityJS ? (LivingEntityJS) e : null;
	}

	@Nullable
	@DocMethod
	public PlayerJS getPlayer(@Nullable Entity entity)
	{
		EntityJS e = getEntity(entity);
		return e instanceof PlayerJS ? (PlayerJS) e : null;
	}

	@DocMethod
	public EntityArrayList createEntityList(Collection<? extends Entity> entities)
	{
		return new EntityArrayList(this, entities);
	}

	@DocMethod
	public EntityArrayList getPlayers()
	{
		return createEntityList(world.playerEntities);
	}

	@DocMethod
	public EntityArrayList getEntities()
	{
		return createEntityList(world.loadedEntityList);
	}

	@DocMethod
	public EntityArrayList getEntities(String filter)
	{
		try
		{
			return createEntityList(EntitySelector.matchEntities(new WorldCommandSender(this), filter, Entity.class));
		}
		catch (CommandException e)
		{
			return new EntityArrayList(this, 0);
		}
	}

	@DocMethod(params = {@Param("x"), @Param("y"), @Param("z")})
	public ExplosionJS createExplosion(double x, double y, double z)
	{
		return new ExplosionJS(this, x, y, z);
	}

	@DocMethod(params = {@Param("x"), @Param("y"), @Param("z"), @Param("effectOnly")})
	public void spawnLightning(double x, double y, double z, boolean effectOnly)
	{
		world.spawnEntity(new EntityLightningBolt(world, x, y, z, effectOnly));
	}
}