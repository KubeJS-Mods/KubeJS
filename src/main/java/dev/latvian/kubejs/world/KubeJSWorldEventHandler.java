package dev.latvian.kubejs.world;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.event.EventsJS;
import dev.latvian.kubejs.server.ServerCreatedEvent;
import dev.latvian.kubejs.server.ServerEventJS;
import dev.latvian.kubejs.server.ServerJS;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

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
			WorldJS w;

			if (event.getWorld().provider.getDimension() == 0)
			{
				ServerJS.instance = new ServerJS(event.getWorld().getMinecraftServer(), (WorldServer) event.getWorld());
				MinecraftForge.EVENT_BUS.post(new ServerCreatedEvent(ServerJS.instance));
				EventsJS.INSTANCE.post(KubeJSEvents.SERVER_LOAD, new ServerEventJS(ServerJS.instance));
				w = ServerJS.instance.overworld;
			}
			else
			{
				w = new WorldJS(ServerJS.instance, (WorldServer) event.getWorld());
				ServerJS.instance.worldMap.put(event.getWorld().provider.getDimension(), w);
				ServerJS.instance.updateWorldList();
			}

			EventsJS.INSTANCE.post(KubeJSEvents.WORLD_LOAD, new WorldEventJS(w));
			MinecraftForge.EVENT_BUS.post(new WorldCreatedEvent(w));
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
	public static void onWorldTick(TickEvent.WorldTickEvent event)
	{
		if (event.phase == TickEvent.Phase.END && !event.world.isRemote)
		{
			EventsJS.INSTANCE.post(KubeJSEvents.WORLD_TICK, new WorldEventJS(event.world));
		}
	}
}