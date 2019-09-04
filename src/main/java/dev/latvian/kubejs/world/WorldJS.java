package dev.latvian.kubejs.world;

import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocField;
import dev.latvian.kubejs.documentation.DocMethod;
import dev.latvian.kubejs.documentation.Param;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.entity.LivingEntityJS;
import dev.latvian.kubejs.player.EntityArrayList;
import dev.latvian.kubejs.player.PlayerDataJS;
import net.minecraft.command.CommandException;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
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
public class WorldJS implements ICommandSender
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
	public long seed()
	{
		return world.getSeed();
	}

	@DocMethod
	public long time()
	{
		return world.getTotalWorldTime();
	}

	@DocMethod
	public long localTime()
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
	public BlockContainerJS block(int x, int y, int z)
	{
		return block(new BlockPos(x, y, z));
	}

	@DocMethod(params = @Param("pos"))
	public BlockContainerJS block(BlockPos pos)
	{
		return new BlockContainerJS(this, pos);
	}

	@Nullable
	public PlayerDataJS playerData(UUID id)
	{
		return null;
	}

	@Nullable
	@DocMethod
	public EntityJS entity(@Nullable Entity entity)
	{
		if (entity == null)
		{
			return null;
		}
		else if (entity instanceof EntityPlayer)
		{
			PlayerDataJS data = playerData(entity.getUniqueID());

			if (data == null)
			{
				throw new NullPointerException("Player from UUID " + entity.getUniqueID() + " not found!");
			}

			return data.player();
		}
		else if (entity instanceof EntityLivingBase)
		{
			return new LivingEntityJS(this, (EntityLivingBase) entity);
		}

		return new EntityJS(this, entity);
	}

	@DocMethod
	public EntityArrayList entities(Collection<? extends Entity> entities)
	{
		return new EntityArrayList(this, entities);
	}

	@DocMethod
	public EntityArrayList players()
	{
		return entities(world.playerEntities);
	}

	@DocMethod
	public EntityArrayList entities()
	{
		return entities(world.loadedEntityList);
	}

	@DocMethod
	public EntityArrayList entities(String filter)
	{
		try
		{
			return entities(EntitySelector.matchEntities(this, filter, Entity.class));
		}
		catch (CommandException e)
		{
			return new EntityArrayList(this, 0);
		}
	}

	@DocMethod(params = {@Param("x"), @Param("y"), @Param("z"), @Param("strength"), @Param("causesFire"), @Param("damagesTerrain")})
	public void explosion(double x, double y, double z, float strength, boolean causesFire, boolean damagesTerrain)
	{
		world.newExplosion(null, x, y, z, strength, causesFire, damagesTerrain);
	}

	@Override
	public String getName()
	{
		return "DIM" + world.provider.getDimension();
	}

	@Override
	public boolean canUseCommand(int permLevel, String commandName)
	{
		return true;
	}

	@Override
	public World getEntityWorld()
	{
		return world;
	}

	@Nullable
	@Override
	public MinecraftServer getServer()
	{
		return null;
	}
}