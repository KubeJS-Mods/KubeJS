package dev.latvian.kubejs.world;

import dev.latvian.kubejs.MinecraftClass;
import dev.latvian.kubejs.documentation.Ignore;
import dev.latvian.kubejs.documentation.Info;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.entity.ItemEntityJS;
import dev.latvian.kubejs.entity.ItemFrameEntityJS;
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
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
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
	@MinecraftClass
	public final World minecraftWorld;

	private AttachedData data;

	public WorldJS(World w)
	{
		minecraftWorld = w;
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
		return new GameRulesJS(minecraftWorld.getGameRules());
	}

	@Nullable
	public ServerJS getServer()
	{
		return null;
	}

	public long getSeed()
	{
		return minecraftWorld.getSeed();
	}

	public long getTime()
	{
		return minecraftWorld.getTotalWorldTime();
	}

	public long getLocalTime()
	{
		return minecraftWorld.getWorldTime();
	}

	public void setTime(long time)
	{
		minecraftWorld.setTotalWorldTime(time);
	}

	public void setLocalTime(long time)
	{
		minecraftWorld.setWorldTime(time);
	}

	public int getDimension()
	{
		return minecraftWorld.provider.getDimension();
	}

	public boolean isOverworld()
	{
		return getDimension() == 0;
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

	public void setRainStrength(@P("strength") float strength)
	{
		minecraftWorld.setRainStrength(strength);
	}

	public BlockContainerJS getBlock(@P("x") int x, @P("y") int y, @P("z") int z)
	{
		return getBlock(new BlockPos(x, y, z));
	}

	public BlockContainerJS getBlock(@P("pos") BlockPos pos)
	{
		return new BlockContainerJS(minecraftWorld, pos);
	}

	public BlockContainerJS getBlock(@P("blockEntity") TileEntity blockEntity)
	{
		return getBlock(blockEntity.getPos());
	}

	@Ignore
	public abstract PlayerDataJS getPlayerData(EntityPlayer player);

	@Nullable
	public EntityJS getEntity(@Nullable Entity e)
	{
		if (e == null)
		{
			return null;
		}
		else if (e instanceof EntityPlayer)
		{
			return getPlayerData((EntityPlayer) e).getPlayer();
		}
		else if (e instanceof EntityLivingBase)
		{
			return new LivingEntityJS(this, (EntityLivingBase) e);
		}
		else if (e instanceof EntityItem)
		{
			return new ItemEntityJS(this, (EntityItem) e);
		}
		else if (e instanceof EntityItemFrame)
		{
			return new ItemFrameEntityJS(this, (EntityItemFrame) e);
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
		return createEntityList(minecraftWorld.playerEntities);
	}

	public EntityArrayList getEntities()
	{
		return createEntityList(minecraftWorld.loadedEntityList);
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
		return new ExplosionJS(minecraftWorld, x, y, z);
	}

	@Nullable
	public EntityJS createEntity(Object id)
	{
		return getEntity(EntityList.createEntityByIDFromName(ID.of(id).mc(), minecraftWorld));
	}

	public void spawnLightning(@P("x") double x, @P("y") double y, @P("z") double z, @P("effectOnly") boolean effectOnly)
	{
		minecraftWorld.addWeatherEffect(new EntityLightningBolt(minecraftWorld, x, y, z, effectOnly));
	}

	public void spawnFireworks(@P("x") double x, @P("y") double y, @P("z") double z, @P("properties") FireworksJS f)
	{
		minecraftWorld.spawnEntity(f.createFireworkRocket(minecraftWorld, x, y, z));
	}
}