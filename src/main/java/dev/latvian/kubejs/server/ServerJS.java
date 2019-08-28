package dev.latvian.kubejs.server;

import dev.latvian.kubejs.documentation.DocMethod;
import dev.latvian.kubejs.documentation.Param;
import dev.latvian.kubejs.player.PlayerDataJS;
import dev.latvian.kubejs.player.PlayerJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextUtilsJS;
import dev.latvian.kubejs.util.MessageSender;
import dev.latvian.kubejs.util.UUIDUtilsJS;
import dev.latvian.kubejs.world.WorldCreatedEvent;
import dev.latvian.kubejs.world.WorldJS;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class ServerJS implements MessageSender
{
	public static ServerJS instance;

	public final transient MinecraftServer server;
	public final transient List<ScheduledEvent> scheduledEvents;
	public final transient Int2ObjectOpenHashMap<WorldJS> worldMap;
	public final transient Map<UUID, PlayerDataJS> playerMap;

	public final Map<String, Object> data;
	public final List<WorldJS> worlds;
	public final WorldJS overworld;
	public final GameRulesJS gameRules;
	public final HashSet<PlayerDataJS> players;

	public ServerJS(MinecraftServer ms, WorldServer w)
	{
		server = ms;
		scheduledEvents = new LinkedList<>();
		worldMap = new Int2ObjectOpenHashMap<>();
		playerMap = new HashMap<>();

		data = new HashMap<>();
		overworld = new WorldJS(this, w);
		worldMap.put(0, overworld);
		worlds = new ArrayList<>();
		worlds.add(overworld);
		gameRules = new GameRulesJS(w.getGameRules());
		players = new HashSet<>();
	}

	public void updatePlayerList()
	{
		players.clear();
		players.addAll(playerMap.values());
	}

	public void updateWorldList()
	{
		worlds.clear();
		worlds.addAll(worldMap.values());
	}

	public boolean running()
	{
		return server.isServerRunning();
	}

	public boolean hardcore()
	{
		return server.isHardcore();
	}

	public boolean singlePlayer()
	{
		return server.isSinglePlayer();
	}

	public boolean dedicated()
	{
		return server.isDedicatedServer();
	}

	public void stop()
	{
		server.stopServer();
	}

	@Override
	public void tell(Text text)
	{
		ITextComponent component = text.component();

		for (EntityPlayerMP player : server.getPlayerList().getPlayers())
		{
			player.sendMessage(component);
		}
	}

	@DocMethod(params = @Param(type = Text.class))
	public void statusMessage(Object message)
	{
		ITextComponent component = TextUtilsJS.INSTANCE.of(message).component();

		for (EntityPlayerMP player : server.getPlayerList().getPlayers())
		{
			player.sendStatusMessage(component, true);
		}
	}

	public WorldJS world(int dimension)
	{
		if (dimension == 0)
		{
			return overworld;
		}

		WorldJS world = worldMap.get(dimension);

		if (world == null)
		{
			world = new WorldJS(this, server.getWorld(dimension));
			worldMap.put(dimension, world);
			updateWorldList();
			MinecraftForge.EVENT_BUS.post(new WorldCreatedEvent(world));
		}

		return world;
	}

	public WorldJS world(World world)
	{
		return world(world.provider.getDimension());
	}

	public PlayerJS player(UUID uuid)
	{
		PlayerDataJS p = playerMap.get(uuid);

		if (p == null)
		{
			throw new NullPointerException("Player from UUID " + uuid + " not found!");
		}

		return p.player();
	}

	public PlayerJS player(String name)
	{
		name = name.trim().toLowerCase();

		if (name.isEmpty())
		{
			throw new NullPointerException("Player can't have empty name!");
		}

		UUID uuid = UUIDUtilsJS.INSTANCE.fromString(name);

		if (uuid != null)
		{
			return player(uuid);
		}

		for (PlayerDataJS p : players)
		{
			if (p.name.equalsIgnoreCase(name))
			{
				return p.player();
			}
		}

		for (PlayerDataJS p : players)
		{
			if (p.name.toLowerCase().contains(name))
			{
				return p.player();
			}
		}

		throw new NullPointerException("Player from name " + name + " not found!");
	}

	@Override
	public int runCommand(String command)
	{
		return server.getCommandManager().executeCommand(server, command);
	}

	public ScheduledEvent schedule(long timer, IScheduledEventCallback event)
	{
		ScheduledEvent e = new ScheduledEvent(this, timer, event);
		scheduledEvents.add(e);
		return e;
	}
}