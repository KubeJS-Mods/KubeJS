package dev.latvian.kubejs.server;

import dev.latvian.kubejs.ATHelper;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.command.CommandRegistryEventJS;
import dev.latvian.kubejs.command.KubeJSCommands;
import dev.latvian.kubejs.player.PlayerDataJS;
import dev.latvian.kubejs.player.SimplePlayerEventJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.script.data.KubeJSDataPackFinder;
import dev.latvian.kubejs.world.AttachWorldDataEvent;
import dev.latvian.kubejs.world.ServerWorldJS;
import dev.latvian.kubejs.world.SimpleWorldEventJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

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
		MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, this::serverAboutToStartEarly);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::serverAboutToStartLate);
		MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
		MinecraftForge.EVENT_BUS.addListener(this::serverStarted);
		MinecraftForge.EVENT_BUS.addListener(this::serverStopping);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, this::tagsUpdated);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::serverTick);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, this::command);
	}

	private void serverAboutToStartEarly(FMLServerAboutToStartEvent event)
	{
		if (ServerJS.instance != null)
		{
			destroyServer();
		}

		ServerJS.instance = new ServerJS(event.getServer());
		event.getServer().getResourcePacks().addPackFinder(new KubeJSDataPackFinder(KubeJS.getGameDirectory().resolve("kubejs").toFile()));
	}

	private void serverAboutToStartLate(FMLServerAboutToStartEvent event)
	{
		try
		{
			SimpleReloadableResourceManager manager = (SimpleReloadableResourceManager) event.getServer().getResourceManager();
			List<IFutureReloadListener> reloadListeners = ATHelper.getReloadListeners(manager);
			List<IFutureReloadListener> initTaskQueue = ATHelper.getInitTaskQueue(manager);
			IFutureReloadListener reloadListener = ServerJS.instance.createReloadListener();
			reloadListeners.add(0, reloadListener);
			initTaskQueue.add(0, reloadListener);
		}
		catch (Exception ex)
		{
			throw new RuntimeException("KubeJS failed to register it's script loader!");
		}
	}

	private void serverStarting(FMLServerStartingEvent event)
	{
		KubeJSCommands.register(event.getCommandDispatcher());
		new CommandRegistryEventJS(event.getServer().isSinglePlayer(), event.getCommandDispatcher()).post(ScriptType.SERVER, KubeJSEvents.COMMAND_REGISTRY);
	}

	private void serverStarted(FMLServerStartedEvent event)
	{
		ServerJS.instance.overworld = new ServerWorldJS(ServerJS.instance, ServerJS.instance.minecraftServer.getWorld(DimensionType.OVERWORLD));
		ServerJS.instance.worldMap.put(DimensionType.OVERWORLD, ServerJS.instance.overworld);
		ServerJS.instance.worlds.add(ServerJS.instance.overworld);

		for (ServerWorld world : ServerJS.instance.minecraftServer.getWorlds())
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

	private void tagsUpdated(TagsUpdatedEvent event)
	{
		if (ServerJS.instance != null && FMLEnvironment.dist.isClient())
		{
			ServerJS.instance.tagsUpdated(event.getTagManager());
		}
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
				catch (Throwable ex)
				{
					if (ex.getClass().getName().equals("jdk.nashorn.api.scripting.NashornException"))
					{
						e.file.pack.manager.type.console.error("Error occurred while handling scheduled event callback in " + e.file.info.location + ": " + ex);
					}
					else
					{
						ex.printStackTrace();
					}
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
				catch (Throwable ex)
				{
					if (ex.getClass().getName().equals("jdk.nashorn.api.scripting.NashornException"))
					{
						e.file.pack.manager.type.console.error("Error occurred while handling scheduled event callback in " + e.file.info.location + ": " + ex);
					}
					else
					{
						ex.printStackTrace();
					}
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