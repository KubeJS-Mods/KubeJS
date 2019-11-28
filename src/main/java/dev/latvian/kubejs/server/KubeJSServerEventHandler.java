package dev.latvian.kubejs.server;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.command.CommandRegistryEventJS;
import dev.latvian.kubejs.player.PlayerDataJS;
import dev.latvian.kubejs.player.SimplePlayerEventJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.world.AttachWorldDataEvent;
import dev.latvian.kubejs.world.ServerWorldJS;
import dev.latvian.kubejs.world.SimpleWorldEventJS;
import dev.latvian.kubejs.world.WorldJS;
import jdk.nashorn.api.scripting.NashornException;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author LatvianModder
 */
public class KubeJSServerEventHandler
{
	public void init()
	{
		MinecraftForge.EVENT_BUS.addListener(this::serverAboutToStart);
		MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
		MinecraftForge.EVENT_BUS.addListener(this::serverStarted);
		MinecraftForge.EVENT_BUS.addListener(this::serverStopping);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::serverTick);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, this::command);
	}

	private void serverAboutToStart(FMLServerAboutToStartEvent event)
	{
		if (ServerJS.instance != null)
		{
			destroyServer();
		}

		ServerJS.instance = new ServerJS(event.getServer());
		ServerJS.instance.registerPacks();
	}

	private void serverStarting(FMLServerStartingEvent event)
	{
		new CommandRegistryEventJS(event.getServer().isSinglePlayer(), event.getCommandDispatcher()).post(ScriptType.SERVER, KubeJSEvents.COMMAND_REGISTRY);
	}

	private void serverStarted(FMLServerStartedEvent event)
	{
		ServerJS.instance.overworld = new ServerWorldJS(ServerJS.instance, event.getServer().getWorld(DimensionType.OVERWORLD));
		ServerJS.instance.worldMap.put(DimensionType.OVERWORLD, ServerJS.instance.overworld);
		ServerJS.instance.worlds.add(ServerJS.instance.overworld);

		for (ServerWorld world : event.getServer().getWorlds())
		{
			if (world != ServerJS.instance.overworld.minecraftWorld)
			{
				ServerWorldJS w = new ServerWorldJS(ServerJS.instance, world);
				ServerJS.instance.worldMap.put(world.getDimension().getType(), w);
			}
		}

		ServerJS.instance.updateWorldList();

		MinecraftForge.EVENT_BUS.post(new AttachServerDataEvent(ServerJS.instance));
		new ServerEventJS().post(ScriptType.SERVER, KubeJSEvents.SERVER_LOAD);

		for (ServerWorldJS world : ServerJS.instance.worlds)
		{
			MinecraftForge.EVENT_BUS.post(new AttachWorldDataEvent(ServerJS.instance.getOverworld()));
			new SimpleWorldEventJS(ServerJS.instance.getOverworld()).post(KubeJSEvents.WORLD_LOAD);
		}
	}

	private void serverStopping(FMLServerStoppingEvent event)
	{
		destroyServer();
	}

	private void destroyServer()
	{
		for (PlayerDataJS p : new ArrayList<>(ServerJS.instance.playerMap.values()))
		{
			new SimplePlayerEventJS(p.getMinecraftPlayer()).post(KubeJSEvents.PLAYER_LOGGED_OUT);
			ServerJS.instance.playerMap.remove(p.getId());
		}

		ServerJS.instance.playerMap.clear();

		for (WorldJS w : ServerJS.instance.worldMap.values())
		{
			new SimpleWorldEventJS(w).post(KubeJSEvents.WORLD_UNLOAD);
			ServerJS.instance.worldMap.remove(w.getDimension());
		}

		ServerJS.instance.updateWorldList();

		new ServerEventJS().post(ScriptType.SERVER, KubeJSEvents.SERVER_UNLOAD);
		ServerJS.instance = null;
	}

	private void serverTick(TickEvent.ServerTickEvent event)
	{
		if (event.phase != TickEvent.Phase.END)
		{
			return;
		}

		ServerJS s = ServerJS.instance;

		if (!s.scheduledEvents.isEmpty())
		{
			long now = System.currentTimeMillis();
			Iterator<ScheduledEvent> eventIterator = s.scheduledEvents.iterator();
			List<ScheduledEvent> list = new ArrayList<>();

			while (eventIterator.hasNext())
			{
				ScheduledEvent e = eventIterator.next();

				if (now >= e.getEndTime())
				{
					list.add(e);
					eventIterator.remove();
				}
			}

			for (ScheduledEvent e : list)
			{
				try
				{
					e.call();
				}
				catch (NashornException ex)
				{
					e.file.pack.manager.type.console.error("Error occurred while handling scheduled event callback in " + e.file.info.location + ": " + ex);
				}
				catch (Throwable ex)
				{
					ex.printStackTrace();
				}
			}
		}

		if (!s.scheduledTickEvents.isEmpty())
		{
			long now = s.getOverworld().getTime();
			Iterator<ScheduledEvent> eventIterator = s.scheduledTickEvents.iterator();
			List<ScheduledEvent> list = new ArrayList<>();

			while (eventIterator.hasNext())
			{
				ScheduledEvent e = eventIterator.next();

				if (now >= e.getEndTime())
				{
					list.add(e);
					eventIterator.remove();
				}
			}

			for (ScheduledEvent e : list)
			{
				try
				{
					e.call();
				}
				catch (NashornException ex)
				{
					e.file.pack.manager.type.console.error("Error occurred while handling scheduled event callback in " + e.file.info.location + ": " + ex);
				}
				catch (Throwable ex)
				{
					ex.printStackTrace();
				}
			}
		}

		new ServerEventJS().post(ScriptType.SERVER, KubeJSEvents.SERVER_TICK);
	}

	private void command(CommandEvent event)
	{
		if (new CommandEventJS(event).post(ScriptType.SERVER, KubeJSEvents.COMMAND_RUN))
		{
			event.setCanceled(true);
		}
	}
}