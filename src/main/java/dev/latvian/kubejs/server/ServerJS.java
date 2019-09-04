package dev.latvian.kubejs.server;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocField;
import dev.latvian.kubejs.documentation.DocMethod;
import dev.latvian.kubejs.documentation.Param;
import dev.latvian.kubejs.player.EntityArrayList;
import dev.latvian.kubejs.player.PlayerDataJS;
import dev.latvian.kubejs.player.PlayerJS;
import dev.latvian.kubejs.player.ServerPlayerDataJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.MessageSender;
import dev.latvian.kubejs.util.UUIDUtilsJS;
import dev.latvian.kubejs.world.AttachWorldDataEvent;
import dev.latvian.kubejs.world.ServerWorldJS;
import dev.latvian.kubejs.world.WorldJS;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.command.CommandException;
import net.minecraft.command.EntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author LatvianModder
 */
@DocClass("Server instance")
public class ServerJS implements MessageSender
{
	public static ServerJS instance;

	public final transient MinecraftServer server;
	public final List<ScheduledEvent> scheduledEvents;
	public final Int2ObjectOpenHashMap<ServerWorldJS> worldMap;
	public final Map<UUID, ServerPlayerDataJS> playerMap;

	@DocField("Temporary data, mods can attach objects to this")
	public final Map<String, Object> data;

	@DocField("List of all currently loaded worlds")
	public final List<ServerWorldJS> worlds;

	@DocField
	public final ServerWorldJS overworld;

	@DocField
	public final GameRulesJS gameRules;

	public ServerJS(MinecraftServer ms, WorldServer w)
	{
		server = ms;
		scheduledEvents = new LinkedList<>();
		worldMap = new Int2ObjectOpenHashMap<>();
		playerMap = new HashMap<>();

		data = new HashMap<>();
		overworld = new ServerWorldJS(this, w);
		worldMap.put(0, overworld);
		worlds = new ArrayList<>();
		worlds.add(overworld);
		gameRules = new GameRulesJS(w.getGameRules());
	}

	@DocMethod
	public void updateWorldList()
	{
		worlds.clear();
		worlds.addAll(worldMap.values());
	}

	@DocMethod
	public boolean isRunning()
	{
		return server.isServerRunning();
	}

	@DocMethod
	public boolean isHardcore()
	{
		return server.isHardcore();
	}

	@DocMethod
	public void setHardcore(boolean hardcore)
	{
		overworld.world.getWorldInfo().setHardcore(hardcore);
	}

	@DocMethod
	public boolean isSinglePlayer()
	{
		return server.isSinglePlayer();
	}

	@DocMethod
	public boolean isDedicated()
	{
		return server.isDedicatedServer();
	}

	@DocMethod
	public String getMOTD()
	{
		return server.getMOTD();
	}

	@DocMethod(params = @Param(value = "text", type = Text.class))
	public void setMOTD(Object text)
	{
		server.setMOTD(Text.of(text).component().getFormattedText());
	}

	@DocMethod
	public void stop()
	{
		server.stopServer();
	}

	@Override
	@DocMethod
	public String name()
	{
		return server.getName();
	}

	@Override
	@DocMethod
	public Text displayName()
	{
		return Text.of(server.getDisplayName());
	}

	@Override
	@DocMethod
	public void tell(Object message)
	{
		ITextComponent component = Text.of(message).component();
		KubeJS.LOGGER.info("Server: " + component.getUnformattedText());

		for (EntityPlayerMP player : server.getPlayerList().getPlayers())
		{
			player.sendMessage(component);
		}
	}

	@Override
	@DocMethod
	public void statusMessage(Object message)
	{
		ITextComponent component = Text.of(message).component();

		for (EntityPlayerMP player : server.getPlayerList().getPlayers())
		{
			player.sendStatusMessage(component, true);
		}
	}

	@Override
	@DocMethod
	public int runCommand(String command)
	{
		return server.getCommandManager().executeCommand(server, command);
	}

	@DocMethod
	public WorldJS world(int dimension)
	{
		if (dimension == 0)
		{
			return overworld;
		}

		ServerWorldJS world = worldMap.get(dimension);

		if (world == null)
		{
			world = new ServerWorldJS(this, server.getWorld(dimension));
			worldMap.put(dimension, world);
			updateWorldList();
			MinecraftForge.EVENT_BUS.post(new AttachWorldDataEvent(world, world.data));
		}

		return world;
	}

	@DocMethod
	public WorldJS world(World world)
	{
		return world(world.provider.getDimension());
	}

	@DocMethod
	public PlayerJS player(UUID uuid)
	{
		ServerPlayerDataJS p = playerMap.get(uuid);

		if (p == null)
		{
			throw new NullPointerException("Player from UUID " + uuid + " not found!");
		}

		return p.player();
	}

	@DocMethod
	public PlayerJS player(String name)
	{
		name = name.trim().toLowerCase();

		if (name.isEmpty())
		{
			throw new NullPointerException("Player can't have empty name!");
		}

		UUID uuid = UUIDUtilsJS.fromString(name);

		if (uuid != null)
		{
			return player(uuid);
		}

		for (PlayerDataJS p : playerMap.values())
		{
			if (p.name.equalsIgnoreCase(name))
			{
				return p.player();
			}
		}

		for (PlayerDataJS p : playerMap.values())
		{
			if (p.name.toLowerCase().contains(name))
			{
				return p.player();
			}
		}

		throw new NullPointerException("Player from name " + name + " not found!");
	}

	@DocMethod
	public EntityArrayList players()
	{
		return new EntityArrayList(overworld, server.getPlayerList().getPlayers());
	}

	@DocMethod
	public EntityArrayList entities()
	{
		EntityArrayList list = new EntityArrayList(overworld, overworld.world.loadedEntityList.size());

		for (WorldJS world : worlds)
		{
			for (Entity entity : world.world.loadedEntityList)
			{
				list.add(world.entity(entity));
			}
		}

		return list;
	}

	@DocMethod
	public EntityArrayList entities(String filter)
	{
		try
		{
			EntityArrayList list = new EntityArrayList(overworld, overworld.world.loadedEntityList.size());

			for (WorldJS world : worlds)
			{
				for (Entity entity : EntitySelector.matchEntities(world, filter, Entity.class))
				{
					list.add(world.entity(entity));
				}
			}

			return list;
		}
		catch (CommandException e)
		{
			return new EntityArrayList(overworld, 0);
		}
	}

	@DocMethod
	public ScheduledEvent schedule(long timer, @Nullable Object data, IScheduledEventCallback event)
	{
		ScheduledEvent e = new ScheduledEvent(this, timer, data, event);
		scheduledEvents.add(e);
		return e;
	}

	@DocMethod
	public ScheduledEvent schedule(long timer, IScheduledEventCallback event)
	{
		return schedule(timer, null, event);
	}

	//TODO: Implement this better with actual tick checking
	@DocMethod
	public ScheduledEvent scheduleInTicks(long ticks, @Nullable Object data, IScheduledEventCallback event)
	{
		return schedule(ticks * 50L, data, event);
	}

	@DocMethod
	public ScheduledEvent scheduleInTicks(long ticks, IScheduledEventCallback event)
	{
		return scheduleInTicks(ticks, null, event);
	}
}