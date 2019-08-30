package dev.latvian.kubejs.world;

import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocField;
import dev.latvian.kubejs.documentation.DocMethod;
import dev.latvian.kubejs.documentation.Param;
import dev.latvian.kubejs.player.EntityArrayList;
import dev.latvian.kubejs.server.ServerJS;
import net.minecraft.command.CommandException;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
@DocClass("This class represents each dimension on server. You can access weather, blocks, entities, etc.")
public class WorldJS implements ICommandSender
{
	public final transient WorldServer world;

	@DocField
	public final ServerJS server;

	@DocField
	public final int dimension;

	@DocField("Temporary data, mods can attach objects to this")
	public final Map<String, Object> data;

	public WorldJS(ServerJS s, WorldServer w)
	{
		server = s;
		world = w;
		dimension = world.provider.getDimension();
		data = new HashMap<>();
	}

	@DocMethod
	public long seed()
	{
		return world.getSeed();
	}

	@DocMethod
	public long localTime()
	{
		return world.getWorldTime();
	}

	@DocMethod
	public long totalTime()
	{
		return world.getTotalWorldTime();
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

	@DocMethod(params = {@Param("x"), @Param("y"), @Param("z")})
	public BlockContainerJS block(int x, int y, int z)
	{
		return new BlockContainerJS(world, new BlockPos(x, y, z));
	}

	@DocMethod
	public EntityArrayList players()
	{
		return server.entities(world.playerEntities);
	}

	@DocMethod
	public EntityArrayList entities()
	{
		return server.entities(world.loadedEntityList);
	}

	@DocMethod
	public EntityArrayList entities(String filter)
	{
		try
		{
			return server.entities(EntitySelector.matchEntities(this, filter, Entity.class));
		}
		catch (CommandException e)
		{
			return new EntityArrayList(server, 0);
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
		return server.server;
	}
}