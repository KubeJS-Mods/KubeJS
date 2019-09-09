package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.event.EventsJS;
import dev.latvian.kubejs.server.ServerJS;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = KubeJS.MOD_ID)
public class KubeJSEntityEventHandler
{
	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event)
	{
		if (EventsJS.post(KubeJSEvents.ENTITY_DEATH, new LivingEntityDeathEventJS(event)))
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onLivingAttack(LivingAttackEvent event)
	{
		if (event.getAmount() > 0F && EventsJS.post(KubeJSEvents.ENTITY_ATTACK, new LivingEntityAttackEventJS(event)))
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void checkLivingSpawn(LivingSpawnEvent.CheckSpawn event)
	{
		if ((ServerJS.instance != null || event.getWorld().isRemote) && EventsJS.post(KubeJSEvents.ENTITY_CHECK_SPAWN, new CheckLivingEntitySpawnEventJS(event)))
		{
			event.setResult(Event.Result.DENY);
		}
	}

	@SubscribeEvent
	public static void onEntitySpawned(EntityJoinWorldEvent event)
	{
		if (event.getWorld() != null && (ServerJS.instance != null || event.getWorld().isRemote) && EventsJS.post(KubeJSEvents.ENTITY_SPAWNED, new EntitySpawnedEventJS(event)))
		{
			event.setCanceled(true);
		}
	}
}