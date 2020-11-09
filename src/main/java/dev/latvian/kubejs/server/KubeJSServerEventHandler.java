package dev.latvian.kubejs.server;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.command.KubeJSCommands;
import dev.latvian.kubejs.player.PlayerDataJS;
import dev.latvian.kubejs.player.SimplePlayerEventJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.world.AttachWorldDataEvent;
import dev.latvian.kubejs.world.ServerWorldJS;
import dev.latvian.kubejs.world.SimpleWorldEventJS;
import dev.latvian.kubejs.world.WorldJS;
import dev.latvian.mods.rhino.RhinoException;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = KubeJS.MOD_ID)
public class KubeJSServerEventHandler
{
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void serverAboutToStartEarly(FMLServerAboutToStartEvent event)
	{
		if (ServerJS.instance != null)
		{
			destroyServer();
		}

		ServerJS.instance = new ServerJS(event.getServer(), ServerScriptManager.instance);
		//event.getServer().getResourcePacks().addPackFinder(new KubeJSDataPackFinder(KubeJS.getGameDirectory().resolve("kubejs").toFile()));
	}

	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent event)
	{
		KubeJSCommands.register(event.getDispatcher());
		// new CommandRegistryEventJS(event).post(ScriptType.SERVER, KubeJSEvents.COMMAND_REGISTRY);
	}

	@SubscribeEvent
	public static void serverStarted(FMLServerStartedEvent event)
	{
		ServerJS.instance.overworld = new ServerWorldJS(ServerJS.instance, ServerJS.instance.minecraftServer.getWorld(World.OVERWORLD));
		ServerJS.instance.worldMap.put("minecraft:overworld", ServerJS.instance.overworld);
		ServerJS.instance.worlds.add(ServerJS.instance.overworld);

		for (ServerWorld world : ServerJS.instance.minecraftServer.getWorlds())
		{
			if (world != ServerJS.instance.overworld.minecraftWorld)
			{
				ServerWorldJS w = new ServerWorldJS(ServerJS.instance, world);
				ServerJS.instance.worldMap.put(world.getDimensionKey().getLocation().toString(), w);
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

	@SubscribeEvent
	public static void serverStopping(FMLServerStoppingEvent event)
	{
		destroyServer();
	}

	public static void destroyServer()
	{
		for (PlayerDataJS p : new ArrayList<>(ServerJS.instance.playerMap.values()))
		{
			new SimplePlayerEventJS(p.getMinecraftPlayer()).post(KubeJSEvents.PLAYER_LOGGED_OUT);
			ServerJS.instance.playerMap.remove(p.getId());
		}

		ServerJS.instance.playerMap.clear();

		for (WorldJS w : new ArrayList<>(ServerJS.instance.worldMap.values()))
		{
			new SimpleWorldEventJS(w).post(KubeJSEvents.WORLD_UNLOAD);
			ServerJS.instance.worldMap.remove(w.getDimension());
		}

		ServerJS.instance.updateWorldList();

		new ServerEventJS().post(ScriptType.SERVER, KubeJSEvents.SERVER_UNLOAD);
		ServerJS.instance = null;
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void serverTick(TickEvent.ServerTickEvent event)
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
				catch (RhinoException ex)
				{
					e.file.pack.manager.type.console.error("Error occurred while handling scheduled event callback: " + ex.getMessage());
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
				catch (RhinoException ex)
				{
					e.file.pack.manager.type.console.error("Error occurred while handling scheduled event callback: " + ex.getMessage());
				}
				catch (Throwable ex)
				{
					ex.printStackTrace();
				}
			}
		}

		new ServerEventJS().post(ScriptType.SERVER, KubeJSEvents.SERVER_TICK);
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void command(CommandEvent event)
	{
		if (new CommandEventJS(event).post(ScriptType.SERVER, KubeJSEvents.COMMAND_RUN))
		{
			event.setCanceled(true);
		}
	}
}