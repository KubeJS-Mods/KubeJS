package dev.latvian.kubejs.server;

import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.net.KubeJSNet;
import dev.latvian.kubejs.net.MessageSendDataFromServer;
import dev.latvian.kubejs.player.AdvancementJS;
import dev.latvian.kubejs.player.EntityArrayList;
import dev.latvian.kubejs.player.FakeServerPlayerDataJS;
import dev.latvian.kubejs.player.ServerPlayerDataJS;
import dev.latvian.kubejs.player.ServerPlayerJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.AttachedData;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.MessageSender;
import dev.latvian.kubejs.util.UUIDUtilsJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.util.WithAttachedData;
import dev.latvian.kubejs.world.AttachWorldDataEvent;
import dev.latvian.kubejs.world.ServerWorldJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.PacketDistributor;

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
public class ServerJS implements MessageSender, WithAttachedData
{
	public static ServerJS instance;

	@MinecraftClass
	public final MinecraftServer minecraftServer;
	public final ServerScriptManager serverScriptManager;
	public final List<ScheduledEvent> scheduledEvents;
	public final List<ScheduledEvent> scheduledTickEvents;
	public final Map<String, ServerWorldJS> worldMap;
	public final Map<UUID, ServerPlayerDataJS> playerMap;
	public final Map<UUID, FakeServerPlayerDataJS> fakePlayerMap;
	public final List<ServerWorldJS> worlds;

	public ServerWorldJS overworld;
	private AttachedData data;

	public ServerJS(MinecraftServer ms, ServerScriptManager m)
	{
		minecraftServer = ms;
		serverScriptManager = m;
		scheduledEvents = new LinkedList<>();
		scheduledTickEvents = new LinkedList<>();
		worldMap = new HashMap<>();
		playerMap = new HashMap<>();
		fakePlayerMap = new HashMap<>();
		worlds = new ArrayList<>();
	}

	public void updateWorldList()
	{
		worlds.clear();
		worlds.addAll(worldMap.values());
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

	public List<ServerWorldJS> getWorlds()
	{
		return worlds;
	}

	public ServerWorldJS getOverworld()
	{
		return overworld;
	}

	public boolean isRunning()
	{
		return minecraftServer.isServerRunning();
	}

	public boolean getHardcore()
	{
		return minecraftServer.isHardcore();
	}

	public boolean isSinglePlayer()
	{
		return minecraftServer.isSinglePlayer();
	}

	public boolean isDedicated()
	{
		return minecraftServer.isDedicatedServer();
	}

	public String getMotd()
	{
		return minecraftServer.getMOTD();
	}

	public void setMotd(Object text)
	{
		minecraftServer.setMOTD(Text.of(text).component().getString());
	}

	public void stop()
	{
		minecraftServer.close();
	}

	@Override
	public Text getName()
	{
		return Text.of(minecraftServer.getName());
	}

	@Override
	public Text getDisplayName()
	{
		return Text.of(minecraftServer.getCommandSource().getDisplayName());
	}

	@Override
	public void tell(Object message)
	{
		ITextComponent component = Text.of(message).component();
		minecraftServer.sendMessage(component, Util.DUMMY_UUID);

		for (ServerPlayerEntity player : minecraftServer.getPlayerList().getPlayers())
		{
			player.sendMessage(component, Util.DUMMY_UUID);
		}
	}

	@Override
	public void setStatusMessage(Object message)
	{
		ITextComponent component = Text.of(message).component();

		for (ServerPlayerEntity player : minecraftServer.getPlayerList().getPlayers())
		{
			player.sendStatusMessage(component, true);
		}
	}

	@Override
	public int runCommand(String command)
	{
		return minecraftServer.getCommandManager().handleCommand(minecraftServer.getCommandSource(), command);
	}

	public WorldJS getWorld(String dimension)
	{
		ServerWorldJS world = worldMap.get(dimension);

		if (world == null)
		{
			world = new ServerWorldJS(this, minecraftServer.getWorld(RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(dimension))));
			worldMap.put(dimension, world);
			updateWorldList();
			MinecraftForge.EVENT_BUS.post(new AttachWorldDataEvent(world));
		}

		return world;
	}

	public WorldJS getWorld(World minecraftWorld)
	{
		ServerWorldJS world = worldMap.get(minecraftWorld.getDimensionKey().getLocation().toString());

		if (world == null)
		{
			world = new ServerWorldJS(this, (ServerWorld) minecraftWorld);
			worldMap.put(minecraftWorld.getDimensionKey().getLocation().toString(), world);
			updateWorldList();
			MinecraftForge.EVENT_BUS.post(new AttachWorldDataEvent(world));
		}

		return world;
	}

	@Nullable
	public ServerPlayerJS getPlayer(UUID uuid)
	{
		ServerPlayerDataJS p = playerMap.get(uuid);

		if (p == null)
		{
			return null;
		}

		return p.getPlayer();
	}

	@Nullable
	public ServerPlayerJS getPlayer(String name)
	{
		name = name.trim().toLowerCase();

		if (name.isEmpty())
		{
			return null;
		}

		UUID uuid = UUIDUtilsJS.fromString(name);

		if (uuid != null)
		{
			return getPlayer(uuid);
		}

		for (ServerPlayerDataJS p : playerMap.values())
		{
			if (p.getName().equalsIgnoreCase(name))
			{
				return p.getPlayer();
			}
		}

		for (ServerPlayerDataJS p : playerMap.values())
		{
			if (p.getName().toLowerCase().contains(name))
			{
				return p.getPlayer();
			}
		}

		return null;
	}

	@Nullable
	public ServerPlayerJS getPlayer(PlayerEntity minecraftPlayer)
	{
		return getPlayer(minecraftPlayer.getUniqueID());
	}

	public EntityArrayList getPlayers()
	{
		return new EntityArrayList(overworld, minecraftServer.getPlayerList().getPlayers());
	}

	public EntityArrayList getEntities()
	{
		EntityArrayList list = new EntityArrayList(overworld, 10);

		for (ServerWorldJS world : worlds)
		{
			list.addAll(world.getEntities());
		}

		return list;
	}

	public EntityArrayList getEntities(String filter)
	{
		EntityArrayList list = new EntityArrayList(overworld, 10);

		for (ServerWorldJS world : worlds)
		{
			list.addAll(world.getEntities(filter));
		}

		return list;
	}

	public ScheduledEvent schedule(long timer, @Nullable Object data, IScheduledEventCallback event)
	{
		ScheduledEvent e = new ScheduledEvent(this, false, timer, System.currentTimeMillis() + timer, data, event);
		scheduledEvents.add(e);
		return e;
	}

	public ScheduledEvent schedule(long timer, IScheduledEventCallback event)
	{
		return schedule(timer, null, event);
	}

	public ScheduledEvent scheduleInTicks(long ticks, @Nullable Object data, IScheduledEventCallback event)
	{
		ScheduledEvent e = new ScheduledEvent(this, true, ticks, overworld.getTime() + ticks, data, event);
		scheduledEvents.add(e);
		return e;
	}

	public ScheduledEvent scheduleInTicks(long ticks, IScheduledEventCallback event)
	{
		return scheduleInTicks(ticks, null, event);
	}

	@Override
	public String toString()
	{
		return "Server";
	}

	@Nullable
	public AdvancementJS getAdvancement(@ID String id)
	{
		Advancement a = minecraftServer.getAdvancementManager().getAdvancement(UtilsJS.getMCID(id));
		return a == null ? null : new AdvancementJS(a);
	}

	public void sendDataToAll(String channel, @Nullable Object data)
	{
		KubeJSNet.MAIN.send(PacketDistributor.ALL.noArg(), new MessageSendDataFromServer(channel, MapJS.nbt(data)));
	}
}