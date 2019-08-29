package dev.latvian.kubejs.item;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.event.EventsJS;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
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
	public static void register(PlayerInteractEvent.RightClickItem event)
	{
	}
}