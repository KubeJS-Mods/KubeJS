package dev.latvian.kubejs.server;

import dev.latvian.kubejs.documentation.Info;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.documentation.T;
import dev.latvian.kubejs.net.KubeJSNetHandler;
import dev.latvian.kubejs.net.MessageSendData;
import dev.latvian.kubejs.player.AdvancementJS;
import dev.latvian.kubejs.player.EntityArrayList;
import dev.latvian.kubejs.player.FakeServerPlayerDataJS;
import dev.latvian.kubejs.player.PlayerDataJS;
import dev.latvian.kubejs.player.PlayerJS;
import dev.latvian.kubejs.player.ServerPlayerDataJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.AttachedData;
import dev.latvian.kubejs.util.ID;
import dev.latvian.kubejs.util.MessageSender;
import dev.latvian.kubejs.util.UUIDUtilsJS;
import dev.latvian.kubejs.util.WithAttachedData;
import dev.latvian.kubejs.util.nbt.NBTBaseJS;
import dev.latvian.kubejs.world.AttachWorldDataEvent;
import dev.latvian.kubejs.world.ServerWorldJS;
import dev.latvian.kubejs.world.WorldCommandSender;
import dev.latvian.kubejs.world.WorldJS;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.advancements.Advancement;
import net.minecraft.command.CommandException;
import net.minecraft.command.EntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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
public class ServerJS implements MessageSender, WithAttachedData
{
	public static ServerJS instance;

	public final transient MinecraftServer server;
	public final List<ScheduledEvent> scheduledEvents;
	public final List<ScheduledEvent> scheduledTickEvents;
	public final Int2ObjectOpenHashMap<ServerWorldJS> worldMap;
	public final Map<UUID, ServerPlayerDataJS> playerMap;
	public final Map<UUID, FakeServerPlayerDataJS> fakePlayerMap;

	private AttachedData data;
	private final List<ServerWorldJS> worlds;
	private final ServerWorldJS overworld;

	public ServerJS(MinecraftServer ms, WorldServer w)
	{
		server = ms;
		scheduledEvents = new LinkedList<>();
		scheduledTickEvents = new LinkedList<>();
		worldMap = new Int2ObjectOpenHashMap<>();
		playerMap = new HashMap<>();
		fakePlayerMap = new HashMap<>();

		overworld = new ServerWorldJS(this, w);
		worldMap.put(0, overworld);
		worlds = new ArrayList<>();
		worlds.add(overworld);
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

	@Info("List of all currently loaded worlds")
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
		return server.isServerRunning();
	}

	public boolean getHardcore()
	{
		return server.isHardcore();
	}

	public void setHardcore(boolean hardcore)
	{
		overworld.world.getWorldInfo().setHardcore(hardcore);
	}

	public boolean isSinglePlayer()
	{
		return server.isSinglePlayer();
	}

	public boolean isDedicated()
	{
		return server.isDedicatedServer();
	}

	public String getMotd()
	{
		return server.getMOTD();
	}

	public void setMotd(@P("text") @T(Text.class) Object text)
	{
		server.setMOTD(Text.of(text).component().getFormattedText());
	}

	public void stop()
	{
		server.stopServer();
	}

	@Override
	public String getName()
	{
		return server.getName();
	}

	@Override
	public Text getDisplayName()
	{
		return Text.of(server.getDisplayName());
	}

	@Override
	public void tell(Object message)
	{
		ITextComponent component = Text.of(message).component();
		server.sendMessage(component);

		for (EntityPlayerMP player : server.getPlayerList().getPlayers())
		{
			player.sendMessage(component);
		}
	}

	@Override
	public void setStatusMessage(Object message)
	{
		ITextComponent component = Text.of(message).component();

		for (EntityPlayerMP player : server.getPlayerList().getPlayers())
		{
			player.sendStatusMessage(component, true);
		}
	}

	@Override
	public int runCommand(String command)
	{
		return server.getCommandManager().executeCommand(server, command);
	}

	public WorldJS getWorld(int dimension)
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
			MinecraftForge.EVENT_BUS.post(new AttachWorldDataEvent(world));
		}

		return world;
	}

	public WorldJS getWorld(World world)
	{
		return getWorld(world.provider.getDimension());
	}

	public PlayerJS getPlayer(UUID uuid)
	{
		ServerPlayerDataJS p = playerMap.get(uuid);

		if (p == null)
		{
			throw new NullPointerException("Player from UUID " + uuid + " not found!");
		}

		return p.getPlayer();
	}

	public PlayerJS getPlayer(String name)
	{
		name = name.trim().toLowerCase();

		if (name.isEmpty())
		{
			throw new NullPointerException("Player can't have empty name!");
		}

		UUID uuid = UUIDUtilsJS.fromString(name);

		if (uuid != null)
		{
			return getPlayer(uuid);
		}

		for (PlayerDataJS p : playerMap.values())
		{
			if (p.getName().equalsIgnoreCase(name))
			{
				return p.getPlayer();
			}
		}

		for (PlayerDataJS p : playerMap.values())
		{
			if (p.getName().toLowerCase().contains(name))
			{
				return p.getPlayer();
			}
		}

		throw new NullPointerException("Player from name " + name + " not found!");
	}

	public PlayerJS getPlayer(EntityPlayer player)
	{
		return getPlayer(player.getUniqueID());
	}

	public EntityArrayList getPlayers()
	{
		return new EntityArrayList(overworld, server.getPlayerList().getPlayers());
	}

	public EntityArrayList getEntities()
	{
		EntityArrayList list = new EntityArrayList(overworld, overworld.world.loadedEntityList.size());

		for (WorldJS world : worlds)
		{
			for (Entity entity : world.world.loadedEntityList)
			{
				list.add(world.getEntity(entity));
			}
		}

		return list;
	}

	public EntityArrayList getEntities(String filter)
	{
		try
		{
			EntityArrayList list = new EntityArrayList(overworld, overworld.world.loadedEntityList.size());

			for (WorldJS world : worlds)
			{
				for (Entity entity : EntitySelector.matchEntities(new WorldCommandSender(world), filter, Entity.class))
				{
					list.add(world.getEntity(entity));
				}
			}

			return list;
		}
		catch (CommandException e)
		{
			return new EntityArrayList(overworld, 0);
		}
	}

	public ScheduledEvent schedule(long timer, @Nullable Object data, IScheduledEventCallback event)
	{
		ScheduledEvent e = new ScheduledEvent(this, timer, System.currentTimeMillis() + timer, data, event);
		scheduledEvents.add(e);
		return e;
	}

	public ScheduledEvent schedule(long timer, IScheduledEventCallback event)
	{
		return schedule(timer, null, event);
	}

	public ScheduledEvent scheduleInTicks(long ticks, @Nullable Object data, IScheduledEventCallback event)
	{
		ScheduledEvent e = new ScheduledEvent(this, ticks, overworld.getTime() + ticks, data, event);
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
	public AdvancementJS getAdvancement(Object id)
	{
		Advancement a = server.getAdvancementManager().getAdvancement(ID.of(id).mc());
		return a == null ? null : new AdvancementJS(a);
	}

	public void sendDataToAll(String channel, @Nullable Object data)
	{
		KubeJSNetHandler.net.sendToAll(new MessageSendData(channel, NBTBaseJS.of(data).asCompound().createNBT()));
	}
}