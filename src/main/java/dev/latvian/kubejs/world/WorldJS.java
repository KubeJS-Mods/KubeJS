package dev.latvian.kubejs.world;

import dev.latvian.kubejs.documentation.Ignore;
import dev.latvian.kubejs.documentation.Info;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.entity.LivingEntityJS;
import dev.latvian.kubejs.player.EntityArrayList;
import dev.latvian.kubejs.player.PlayerDataJS;
import dev.latvian.kubejs.player.PlayerJS;
import dev.latvian.kubejs.server.GameRulesJS;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.util.AttachedData;
import dev.latvian.kubejs.util.ID;
import dev.latvian.kubejs.util.WithAttachedData;
import net.minecraft.command.CommandException;
import net.minecraft.command.EntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @author LatvianModder
 */
@Info("This class represents a dimension. You can access weather, blocks, entities, etc. Client and server sides have different worlds")
public abstract class WorldJS implements WithAttachedData
{
	public final World world;

	private AttachedData data;

	public WorldJS(World w)
	{
		world = w;
	}

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
		return new GameRulesJS(world.getGameRules());
	}

	@Nullable
	public ServerJS getServer()
	{
		return null;
	}

	public long getSeed()
	{
		return world.getSeed();
	}

	public long getTime()
	{
		return world.getTotalWorldTime();
	}

	public long getLocalTime()
	{
		return world.getWorldTime();
	}

	public void setTime(long time)
	{
		world.setTotalWorldTime(time);
	}

	public void setLocalTime(long time)
	{
		world.setWorldTime(time);
	}

	public int getDimension()
	{
		return world.provider.getDimension();
	}

	public boolean isOverworld()
	{
		return getDimension() == 0;
	}

	public boolean isDaytime()
	{
		return world.isDaytime();
	}

	public boolean isRaining()
	{
		return world.isRaining();
	}

	public boolean isThundering()
	{
		return world.isThundering();
	}

	public void setRainStrength(@P("strength") float strength)
	{
		world.setRainStrength(strength);
	}

	public BlockContainerJS getBlock(@P("x") int x, @P("y") int y, @P("z") int z)
	{
		return getBlock(new BlockPos(x, y, z));
	}

	public BlockContainerJS getBlock(@P("pos") BlockPos pos)
	{
		return new BlockContainerJS(world, pos);
	}

	@Ignore
	public abstract PlayerDataJS getPlayerData(EntityPlayer player);

	@Nullable
	public EntityJS getEntity(@Nullable Entity entity)
	{
		if (entity == null)
		{
			return null;
		}
		else if (entity instanceof EntityPlayer)
		{
			return getPlayerData((EntityPlayer) entity).getPlayer();
		}
		else if (entity instanceof EntityLivingBase)
		{
			return new LivingEntityJS(this, (EntityLivingBase) entity);
		}

		return new EntityJS(this, entity);
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
		return createEntityList(world.playerEntities);
	}

	public EntityArrayList getEntities()
	{
		return createEntityList(world.loadedEntityList);
	}

	public EntityArrayList getEntities(@P("filter") String filter)
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

	public ExplosionJS createExplosion(@P("x") double x, @P("y") double y, @P("z") double z)
	{
		return new ExplosionJS(world, x, y, z);
	}

	@Nullable
	public EntityJS createEntity(Object id)
	{
		return getEntity(EntityList.createEntityByIDFromName(ID.of(id).mc(), world));
	}

	public void spawnLightning(@P("x") double x, @P("y") double y, @P("z") double z, @P("effectOnly") boolean effectOnly)
	{
		world.addWeatherEffect(new EntityLightningBolt(world, x, y, z, effectOnly));
	}

	public void spawnFireworks(@P("x") double x, @P("y") double y, @P("z") double z, @P("properties") FireworksJS f)
	{
		world.spawnEntity(f.createFireworkRocket(world, x, y, z));
	}
}