package dev.latvian.kubejs.server;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.event.EventsJS;
import jdk.nashorn.api.scripting.NashornException;
import net.minecraftforge.event.CommandEvent;
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
public class KubeJSServerEventHandler
{
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onServerTick(TickEvent.ServerTickEvent event)
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
					KubeJS.LOGGER.error("Error occurred while handling scheduled event callback in " + e.file.getPath() + ": " + ex);
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
					KubeJS.LOGGER.error("Error occurred while handling scheduled event callback in " + e.file.getPath() + ": " + ex);
				}
				catch (Throwable ex)
				{
					ex.printStackTrace();
				}
			}
		}

		EventsJS.post(KubeJSEvents.SERVER_TICK, new SimpleServerEventJS(s));
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onCommand(CommandEvent event)
	{
		if (EventsJS.post(KubeJSEvents.COMMAND_RUN, new CommandEventJS(ServerJS.instance, event)))
		{
			event.setCanceled(true);
		}
	}
}