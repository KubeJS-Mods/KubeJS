package com.latmod.mods.kubejs.world;

import com.latmod.mods.kubejs.KubeJS;
import com.latmod.mods.kubejs.KubeJSEvents;
import com.latmod.mods.kubejs.events.EventsJS;
import com.latmod.mods.kubejs.util.ServerJS;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
				KubeJS.server = new ServerJS(event.getWorld().getMinecraftServer(), (WorldServer) event.getWorld());
				EventsJS.INSTANCE.post(KubeJSEvents.SERVER_LOAD, new ServerEventJS(KubeJS.server));
			}

			EventsJS.INSTANCE.post(KubeJSEvents.WORLD_LOAD, new WorldEventJS(event.getWorld()));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onWorldUnloaded(WorldEvent.Unload event)
	{
		if (event.getWorld() instanceof WorldServer && KubeJS.server != null)
		{
			EventsJS.INSTANCE.post(KubeJSEvents.WORLD_UNLOAD, new WorldEventJS(event.getWorld()));

			if (event.getWorld().provider.getDimension() == 0)
			{
				EventsJS.INSTANCE.post(KubeJSEvents.SERVER_UNLOAD, new ServerEventJS(KubeJS.server));
				KubeJS.server.stop();
				KubeJS.server = null;
			}
		}
	}
}