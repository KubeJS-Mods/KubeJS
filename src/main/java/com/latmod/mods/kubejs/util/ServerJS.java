package com.latmod.mods.kubejs.util;

import com.latmod.mods.kubejs.player.PlayerJS;
import com.latmod.mods.kubejs.text.TextUtils;
import com.latmod.mods.kubejs.world.GameRulesJS;
import com.latmod.mods.kubejs.world.IScheduledEventCallback;
import com.latmod.mods.kubejs.world.ScheduledEvent;
import com.latmod.mods.kubejs.world.WorldJS;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.WorldServer;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class ServerJS
{
	private final MinecraftServer server;
	public final WorldJS overworld;
	public final GameRulesJS gameRules;
	public final Map<UUID, PlayerJS> playerMap;
	public final List<PlayerJS> players;
	public final List<ScheduledEvent> scheduledEvents;

	public ServerJS(MinecraftServer ms, WorldServer w)
	{
		server = ms;
		overworld = new WorldJS(this, w);
		gameRules = new GameRulesJS(w.getGameRules());
		playerMap = new Object2ObjectOpenHashMap<>();
		players = new ObjectArrayList<>();
		scheduledEvents = new ObjectArrayList<>();
	}

	public boolean isRunning()
	{
		return server.isServerRunning();
	}

	public boolean isHardcore()
	{
		return server.isHardcore();
	}

	public boolean isSingleplayer()
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

		return new WorldJS(this, server.getWorld(dimension));
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