package dev.latvian.kubejs.util;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerJS;
import dev.latvian.kubejs.text.TextUtils;
import dev.latvian.kubejs.world.GameRulesJS;
import dev.latvian.kubejs.world.IScheduledEventCallback;
import dev.latvian.kubejs.world.ScheduledEvent;
import dev.latvian.kubejs.world.WorldJS;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class ServerJS
{
	public static ServerJS instance;

	public final MinecraftServer server;
	public final List<ScheduledEvent> scheduledEvents;

	public final Int2ObjectOpenHashMap<WorldJS> worldMap;
	public final List<WorldJS> worlds;
	public final WorldJS overworld;
	public final GameRulesJS gameRules;
	public final Map<UUID, PlayerJS> playerMap;
	public final List<PlayerJS> players;

	public ServerJS(MinecraftServer ms, WorldServer w)
	{
		server = ms;
		scheduledEvents = new ObjectArrayList<>();

		overworld = new WorldJS(this, w);
		worldMap = new Int2ObjectOpenHashMap<>();
		worldMap.put(0, overworld);
		worlds = new ObjectArrayList<>();
		worlds.add(overworld);
		gameRules = new GameRulesJS(w.getGameRules());
		playerMap = new Object2ObjectOpenHashMap<>();
		players = new ObjectArrayList<>();
	}

	public void updatePlayerList()
	{
		players.clear();
		players.addAll(playerMap.values());
		players.sort(EntityJS.COMPARATOR);
	}

	public void updateWorldList()
	{
		worlds.clear();
		worlds.addAll(worldMap.values());
	}

	public boolean isRunning()
	{
		return server.isServerRunning();
	}

	public boolean isHardcore()
	{
		return server.isHardcore();
	}

	public boolean isSinglePlayer()
	{
		return server.isSinglePlayer();
	}

	public boolean isDedicated()
	{
		return server.isDedicatedServer();
	}

	public void stop()
	{
		server.stopServer();
	}

	public void sendMessage(Object... message)
	{
		ITextComponent component = TextUtils.INSTANCE.of(message).component();

		for (EntityPlayerMP player : server.getPlayerList().getPlayers())
		{
			player.sendMessage(component);
		}
	}

	public void sendStatusMessage(Object... message)
	{
		ITextComponent component = TextUtils.INSTANCE.of(message).component();

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
		}

		return world;
	}

	public WorldJS world(World world)
	{
		return world(world.provider.getDimension());
	}

	public PlayerJS player(UUID uuid)
	{
		PlayerJS p = playerMap.get(uuid);

		if (p == null)
		{
			throw new NullPointerException("Player from UUID " + uuid + " not found!");
		}

		return p;
	}

	public PlayerJS player(String name)
	{
		throw new NullPointerException("Player from name " + name + " not found!");
	}

	public void runCommand(String command)
	{
		server.getCommandManager().executeCommand(server, command);
	}

	public void schedule(long timer, IScheduledEventCallback event)
	{
		scheduledEvents.add(new ScheduledEvent(this, timer, event));
	}
}