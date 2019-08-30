package dev.latvian.kubejs.world;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.event.EventsJS;
import dev.latvian.kubejs.player.PlayerDataJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import dev.latvian.kubejs.server.ServerCreatedEvent;
import dev.latvian.kubejs.server.ServerEventJS;
import dev.latvian.kubejs.server.ServerJS;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = KubeJS.MOD_ID)
public class KubeJSWorldEventHandler
{
	public static void onServerStarting(MinecraftServer server)
	{
		ServerJS.instance = new ServerJS(server, (WorldServer) server.getEntityWorld());
		MinecraftForge.EVENT_BUS.post(new ServerCreatedEvent(ServerJS.instance));
		EventsJS.post(KubeJSEvents.SERVER_LOAD, new ServerEventJS(ServerJS.instance));
		MinecraftForge.EVENT_BUS.post(new WorldCreatedEvent(ServerJS.instance.overworld));
		EventsJS.post(KubeJSEvents.WORLD_LOAD, new WorldEventJS(ServerJS.instance.overworld));

		for (WorldServer world : server.worlds)
		{
			KubeJS.LOGGER.info(world.provider.getDimension() + " ; " + world.provider.getDimensionType());

			if (world != ServerJS.instance.overworld.world)
			{
				WorldJS w = new WorldJS(ServerJS.instance, world);
				ServerJS.instance.worldMap.put(world.provider.getDimension(), w);
				ServerJS.instance.updateWorldList();
				MinecraftForge.EVENT_BUS.post(new WorldCreatedEvent(w));
				EventsJS.post(KubeJSEvents.WORLD_LOAD, new WorldEventJS(w));
			}
		}

		ServerJS.instance.updateWorldList();
	}

	public static void onServerStopping()
	{
		for (PlayerDataJS p : new ArrayList<>(ServerJS.instance.playerMap.values()))
		{
			EventsJS.post(KubeJSEvents.PLAYER_LOGGED_OUT, new PlayerEventJS(p.player()));
			ServerJS.instance.playerMap.remove(p.uuid);
		}

		ServerJS.instance.playerMap.clear();

		for (WorldJS w : ServerJS.instance.worldMap.values())
		{
			EventsJS.post(KubeJSEvents.WORLD_UNLOAD, new WorldEventJS(w));
			ServerJS.instance.worldMap.remove(w.dimension);
		}

		ServerJS.instance.updateWorldList();

		EventsJS.post(KubeJSEvents.SERVER_UNLOAD, new ServerEventJS(ServerJS.instance));
		ServerJS.instance = null;
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onWorldLoaded(WorldEvent.Load event)
	{
		if (ServerJS.instance != null && event.getWorld() instanceof WorldServer && !ServerJS.instance.worldMap.containsKey(event.getWorld().provider.getDimension()))
		{
			WorldJS w = new WorldJS(ServerJS.instance, (WorldServer) event.getWorld());
			ServerJS.instance.worldMap.put(event.getWorld().provider.getDimension(), w);
			ServerJS.instance.updateWorldList();
			MinecraftForge.EVENT_BUS.post(new WorldCreatedEvent(w));
			EventsJS.post(KubeJSEvents.WORLD_LOAD, new WorldEventJS(w));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onWorldUnloaded(WorldEvent.Unload event)
	{
		if (ServerJS.instance != null && event.getWorld() instanceof WorldServer && ServerJS.instance.worldMap.containsKey(event.getWorld().provider.getDimension()))
		{
			WorldJS w = ServerJS.instance.world(event.getWorld());
			EventsJS.post(KubeJSEvents.WORLD_UNLOAD, new WorldEventJS(w));
			ServerJS.instance.worldMap.remove(w.dimension);
			ServerJS.instance.updateWorldList();
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onWorldTick(TickEvent.WorldTickEvent event)
	{
		if (event.phase == TickEvent.Phase.END && !event.world.isRemote)
		{
			WorldJS w = ServerJS.instance.world(event.world);
			EventsJS.post(KubeJSEvents.WORLD_TICK, new WorldEventJS(w));
		}
	}
}