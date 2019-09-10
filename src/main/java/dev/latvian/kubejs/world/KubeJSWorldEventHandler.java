package dev.latvian.kubejs.world;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.event.EventsJS;
import dev.latvian.kubejs.player.PlayerDataJS;
import dev.latvian.kubejs.player.SimplePlayerEventJS;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.kubejs.server.AttachServerDataEvent;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.server.SimpleServerEventJS;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ExplosionEvent;
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
		MinecraftForge.EVENT_BUS.post(new AttachServerDataEvent(ServerJS.instance));
		EventsJS.post(KubeJSEvents.SERVER_LOAD, new SimpleServerEventJS(ServerJS.instance));
		MinecraftForge.EVENT_BUS.post(new AttachWorldDataEvent(ServerJS.instance.overworld));
		EventsJS.post(KubeJSEvents.WORLD_LOAD, new SimpleWorldEventJS(ServerJS.instance.overworld));

		for (WorldServer world : server.worlds)
		{
			if (world != ServerJS.instance.overworld.world)
			{
				ServerWorldJS w = new ServerWorldJS(ServerJS.instance, world);
				ServerJS.instance.worldMap.put(world.provider.getDimension(), w);
				ServerJS.instance.updateWorldList();
				MinecraftForge.EVENT_BUS.post(new AttachWorldDataEvent(w));
				EventsJS.post(KubeJSEvents.WORLD_LOAD, new SimpleWorldEventJS(w));
			}
		}

		ServerJS.instance.updateWorldList();
		ScriptManager.instance.runtime.put("server", ServerJS.instance);
	}

	public static void onServerStopping()
	{
		for (PlayerDataJS p : new ArrayList<>(ServerJS.instance.playerMap.values()))
		{
			EventsJS.post(KubeJSEvents.PLAYER_LOGGED_OUT, new SimplePlayerEventJS(p.getPlayerEntity()));
			ServerJS.instance.playerMap.remove(p.id);
		}

		ServerJS.instance.playerMap.clear();

		for (WorldJS w : ServerJS.instance.worldMap.values())
		{
			EventsJS.post(KubeJSEvents.WORLD_UNLOAD, new SimpleWorldEventJS(w));
			ServerJS.instance.worldMap.remove(w.dimension);
		}

		ServerJS.instance.updateWorldList();

		EventsJS.post(KubeJSEvents.SERVER_UNLOAD, new SimpleServerEventJS(ServerJS.instance));
		ServerJS.instance = null;
		ScriptManager.instance.runtime.remove("server");
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onWorldLoaded(WorldEvent.Load event)
	{
		if (ServerJS.instance != null && event.getWorld() instanceof WorldServer && !ServerJS.instance.worldMap.containsKey(event.getWorld().provider.getDimension()))
		{
			ServerWorldJS w = new ServerWorldJS(ServerJS.instance, (WorldServer) event.getWorld());
			ServerJS.instance.worldMap.put(event.getWorld().provider.getDimension(), w);
			ServerJS.instance.updateWorldList();
			MinecraftForge.EVENT_BUS.post(new AttachWorldDataEvent(w));
			EventsJS.post(KubeJSEvents.WORLD_LOAD, new SimpleWorldEventJS(w));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onWorldUnloaded(WorldEvent.Unload event)
	{
		if (ServerJS.instance != null && event.getWorld() instanceof WorldServer && ServerJS.instance.worldMap.containsKey(event.getWorld().provider.getDimension()))
		{
			WorldJS w = ServerJS.instance.getWorld(event.getWorld());
			EventsJS.post(KubeJSEvents.WORLD_UNLOAD, new SimpleWorldEventJS(w));
			ServerJS.instance.worldMap.remove(w.dimension);
			ServerJS.instance.updateWorldList();
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onWorldTick(TickEvent.WorldTickEvent event)
	{
		if (event.phase == TickEvent.Phase.END && !event.world.isRemote)
		{
			WorldJS w = ServerJS.instance.getWorld(event.world);
			EventsJS.post(KubeJSEvents.WORLD_TICK, new SimpleWorldEventJS(w));
		}
	}

	@SubscribeEvent
	public static void onExplosionPre(ExplosionEvent.Start event)
	{
		if (EventsJS.post(KubeJSEvents.WORLD_EXPLOSION_PRE, new ExplosionEventJS.Pre(event)))
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onExplosionPre(ExplosionEvent.Detonate event)
	{
		EventsJS.post(KubeJSEvents.WORLD_EXPLOSION_POST, new ExplosionEventJS.Post(event));
	}
}