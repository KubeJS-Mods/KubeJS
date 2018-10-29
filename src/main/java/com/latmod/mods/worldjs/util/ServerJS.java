package com.latmod.mods.worldjs.util;

import com.latmod.mods.worldjs.player.PlayerJS;
import com.latmod.mods.worldjs.text.TextUtils;
import com.latmod.mods.worldjs.world.WorldJS;
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
	public final MinecraftServer server;
	public final WorldJS overworld;
	public final Map<UUID, PlayerJS> playerMap;
	public final List<PlayerJS> players;
	private boolean running;

	public ServerJS(MinecraftServer ms, WorldServer w)
	{
		server = ms;
		overworld = new WorldJS(this, w);
		playerMap = new Object2ObjectOpenHashMap<>();
		players = new ObjectArrayList<>();
		running = true;
	}

	public boolean isRunning()
	{
		return running;
	}

	public void stop()
	{
		running = false;
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

	public WorldJS getWorld(int dimension)
	{
		if (dimension == 0)
		{
			return overworld;
		}

		return new WorldJS(this, server.getWorld(dimension));
	}

	public PlayerJS getPlayer(UUID uuid)
	{
		PlayerJS p = playerMap.get(uuid);

		if (p == null)
		{
			throw new NullPointerException("Player from UUID " + uuid + " not found!");
		}

		return p;
	}

	public PlayerJS getPlayer(String name)
	{
		throw new NullPointerException("Player from name " + name + " not found!");
	}

	public void runCommand(String command)
	{
		server.getCommandManager().executeCommand(server, command);
	}
}