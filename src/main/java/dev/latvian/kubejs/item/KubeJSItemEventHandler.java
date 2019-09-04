package dev.latvian.kubejs.item;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.event.EventsJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = KubeJS.MOD_ID)
public class KubeJSItemEventHandler
{
	@SubscribeEvent
	public static void registry(RegistryEvent.Register<Item> event)
	{
		EventsJS.post(KubeJSEvents.ITEM_REGISTRY, new ItemRegistryEventJS(event.getRegistry()));
	}

	@SubscribeEvent
	public static void rightClick(PlayerInteractEvent.RightClickItem event)
	{
		if (EventsJS.post(KubeJSEvents.ITEM_RIGHT_CLICK, new ItemRightClickEventJS(event)))
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void rightClickEmpty(PlayerInteractEvent.RightClickEmpty event)
	{
		EventsJS.post(KubeJSEvents.ITEM_RIGHT_CLICK_EMPTY, new ItemRightClickEmptyEventJS(event));
	}

	@SubscribeEvent
	public static void leftClickEmpty(PlayerInteractEvent.LeftClickEmpty event)
	{
		EventsJS.post(KubeJSEvents.ITEM_LEFT_CLICK, new PlayerEventJS(event.getEntityPlayer()));
	}

	@SubscribeEvent
	public static void pickup(EntityItemPickupEvent event)
	{
		if (EventsJS.post(KubeJSEvents.ITEM_PICKUP, new ItemPickupEventJS(event)))
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void toss(ItemTossEvent event)
	{
		if (EventsJS.post(KubeJSEvents.ITEM_TOSS, new ItemTossEventJS(event)))
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void entityInteract(PlayerInteractEvent.EntityInteract event)
	{
		if (EventsJS.post(KubeJSEvents.ITEM_ENTITY_INTERACT, new ItemEntityInteractEventJS(event)))
		{
			event.setCanceled(true);
		}
	}
}