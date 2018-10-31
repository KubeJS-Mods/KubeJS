package com.latmod.mods.worldjs.world;

import com.latmod.mods.worldjs.events.EventsJS;
import com.latmod.mods.worldjs.mod.WorldJSMod;
import com.latmod.mods.worldjs.util.ServerJS;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = WorldJSMod.MOD_ID)
public class WorldJSWorldEventHandler
{
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onWorldLoaded(WorldEvent.Load event)
	{
		if (event.getWorld() instanceof WorldServer)
		{
			if (event.getWorld().provider.getDimension() == 0)
			{
				WorldJSMod.server = new ServerJS(event.getWorld().getMinecraftServer(), (WorldServer) event.getWorld());
				WorldJSMod.loadScripts();
			}

			EventsJS.INSTANCE.post("world.load", new WorldEventJS(event.getWorld()));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onWorldUnloaded(WorldEvent.Unload event)
	{
		if (event.getWorld() instanceof WorldServer)
		{
			EventsJS.INSTANCE.post("world.unload", new WorldEventJS(event.getWorld()));

			if (WorldJSMod.server != null && event.getWorld().provider.getDimension() == 0)
			{
				WorldJSMod.server.stop();
				WorldJSMod.server = null;
			}
		}
	}
}