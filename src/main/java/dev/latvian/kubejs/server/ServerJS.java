package dev.latvian.kubejs.server;

import dev.latvian.kubejs.MinecraftClass;
import dev.latvian.kubejs.documentation.Ignore;
import dev.latvian.kubejs.documentation.Info;
import dev.latvian.kubejs.documentation.O;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.documentation.T;
import dev.latvian.kubejs.net.KubeJSNet;
import dev.latvian.kubejs.net.MessageSendDataFromClient;
import dev.latvian.kubejs.player.AdvancementJS;
import dev.latvian.kubejs.player.EntityArrayList;
import dev.latvian.kubejs.player.FakeServerPlayerDataJS;
import dev.latvian.kubejs.player.PlayerDataJS;
import dev.latvian.kubejs.player.PlayerJS;
import dev.latvian.kubejs.player.ServerPlayerDataJS;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.AttachedData;
import dev.latvian.kubejs.util.MessageSender;
import dev.latvian.kubejs.util.UUIDUtilsJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.util.WithAttachedData;
import dev.latvian.kubejs.util.nbt.NBTBaseJS;
import dev.latvian.kubejs.world.AttachWorldDataEvent;
import dev.latvian.kubejs.world.ServerWorldJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;
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

	@Ignore
	public final ScriptManager scriptManager;

	@Ignore
	public final List<ScheduledEvent> scheduledEvents;

	@Ignore
	public final List<ScheduledEvent> scheduledTickEvents;

	@Ignore
	public final Map<DimensionType, ServerWorldJS> worldMap;

	@Ignore
	public final Map<UUID, ServerPlayerDataJS> playerMap;

	@Ignore
	public final Map<UUID, FakeServerPlayerDataJS> fakePlayerMap;

	@Ignore
	public final List<ServerWorldJS> worlds;

	public ServerWorldJS overworld;
	private AttachedData data;

	public ServerJS(MinecraftServer ms)
	{
		minecraftServer = ms;
		scriptManager = new ScriptManager(ScriptType.SERVER);
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
		return minecraftServer.isServerRunning();
	}

	public boolean getHardcore()
	{
		return minecraftServer.isHardcore();
	}

	public void setHardcore(boolean hardcore)
	{
		overworld.minecraftWorld.getWorldInfo().setHardcore(hardcore);
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

	public void setMotd(@P("text") @T(Text.class) Object text)
	{
		minecraftServer.setMOTD(Text.of(text).component().getFormattedText());
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
		minecraftServer.sendMessage(component);

		for (ServerPlayerEntity player : minecraftServer.getPlayerList().getPlayers())
		{
			player.sendMessage(component);
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

	public WorldJS getWorld(@P("dimension") DimensionType dimension)
	{
		if (dimension == DimensionType.OVERWORLD)
		{
			return overworld;
		}

		ServerWorldJS world = worldMap.get(dimension);

		if (world == null)
		{
			world = new ServerWorldJS(this, minecraftServer.getWorld(dimension));
			worldMap.put(dimension, world);
			updateWorldList();
			MinecraftForge.EVENT_BUS.post(new AttachWorldDataEvent(world));
		}

		return world;
	}

	public WorldJS getWorld(@P("minecraftWorld") IWorld minecraftWorld)
	{
		return getWorld(minecraftWorld.getDimension().getType());
	}

	@Nullable
	public PlayerJS getPlayer(@P("uuid") UUID uuid)
	{
		ServerPlayerDataJS p = playerMap.get(uuid);

		if (p == null)
		{
			return null;
		}

		return p.getPlayer();
	}

	@Nullable
	public PlayerJS getPlayer(@P("name") String name)
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

		return null;
	}

	@Nullable
	public PlayerJS getPlayer(@P("minecraftPlayer") PlayerEntity minecraftPlayer)
	{
		return getPlayer(minecraftPlayer.getUniqueID());
	}

	public EntityArrayList getPlayers()
	{
		return new EntityArrayList(overworld, minecraftServer.getPlayerList().getPlayers());
	}

	@Ignore
	public EntityArrayList getEntities()
	{
		EntityArrayList list = new EntityArrayList(overworld, 10);

		for (ServerWorldJS world : worlds)
		{
			list.addAll(world.getEntities());
		}

		return list;
	}

	public EntityArrayList getEntities(@O @P("filter") String filter)
	{
		EntityArrayList list = new EntityArrayList(overworld, 10);

		for (ServerWorldJS world : worlds)
		{
			list.addAll(world.getEntities(filter));
		}

		return list;
	}

	public ScheduledEvent schedule(@P("timer") long timer, @O @P("data") @Nullable Object data, @P("callback") IScheduledEventCallback event)
	{
		ScheduledEvent e = new ScheduledEvent(this, false, timer, System.currentTimeMillis() + timer, data, event);
		scheduledEvents.add(e);
		return e;
	}

	@Ignore
	public ScheduledEvent schedule(long timer, IScheduledEventCallback event)
	{
		return schedule(timer, null, event);
	}

	public ScheduledEvent scheduleInTicks(@P("ticks") long ticks, @O @P("data") @Nullable Object data, @P("callback") IScheduledEventCallback event)
	{
		ScheduledEvent e = new ScheduledEvent(this, true, ticks, overworld.getTime() + ticks, data, event);
		scheduledEvents.add(e);
		return e;
	}

	@Ignore
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
	public AdvancementJS getAdvancement(@P("id") Object id)
	{
		Advancement a = minecraftServer.getAdvancementManager().getAdvancement(UtilsJS.getID(id));
		return a == null ? null : new AdvancementJS(a);
	}

	public void sendDataToAll(@P("channel") String channel, @P("data") @Nullable Object data)
	{
		KubeJSNet.MAIN.send(PacketDistributor.ALL.noArg(), new MessageSendDataFromClient(channel, NBTBaseJS.of(data).asCompound().createNBT()));
	}
}