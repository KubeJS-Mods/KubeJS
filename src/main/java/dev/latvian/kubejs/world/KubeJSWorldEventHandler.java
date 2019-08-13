package dev.latvian.kubejs.world;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.events.EventsJS;
import dev.latvian.kubejs.util.ServerJS;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = KubeJS.MOD_ID)
public class KubeJSWorldEventHandler
{
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onWorldLoaded(WorldEvent.Load event)
	{
		if (event.getWorld() instanceof WorldServer)
		{
			if (event.getWorld().provider.getDimension() == 0)
			{
				ServerJS.instance = new ServerJS(event.getWorld().getMinecraftServer(), (WorldServer) event.getWorld());
				EventsJS.INSTANCE.post(KubeJSEvents.SERVER_LOAD, new ServerEventJS(ServerJS.instance));
			}
			else
			{
				ServerJS.instance.worldMap.put(event.getWorld().provider.getDimension(), new WorldJS(ServerJS.instance, (WorldServer) event.getWorld()));
				ServerJS.instance.updateWorldList();
			}

			EventsJS.INSTANCE.post(KubeJSEvents.WORLD_LOAD, new WorldEventJS(event.getWorld()));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onWorldUnloaded(WorldEvent.Unload event)
	{
		if (ServerJS.instance != null && event.getWorld() instanceof WorldServer)
		{
			EventsJS.INSTANCE.post(KubeJSEvents.WORLD_UNLOAD, new WorldEventJS(event.getWorld()));
			ServerJS.instance.worldMap.remove(event.getWorld().provider.getDimension());
			ServerJS.instance.updateWorldList();

			if (event.getWorld().provider.getDimension() == 0)
			{
				EventsJS.INSTANCE.post(KubeJSEvents.SERVER_UNLOAD, new ServerEventJS(ServerJS.instance));
				ServerJS.instance = null;
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onServerTick(TickEvent.ServerTickEvent event)
	{
		if (event.phase == TickEvent.Phase.END && !ServerJS.instance.scheduledEvents.isEmpty())
		{
			long now = System.currentTimeMillis();
			Iterator<ScheduledEvent> eventIterator = ServerJS.instance.scheduledEvents.iterator();
			List<ScheduledEvent> list = new ArrayList<>();

			while (eventIterator.hasNext())
			{
				ScheduledEvent e = eventIterator.next();

				if (now >= e.endTime)
				{
					list.add(e);
					eventIterator.remove();
				}
			}

			for (ScheduledEvent e : list)
			{
				e.function.onCallback(e);
			}
		}
	}
}