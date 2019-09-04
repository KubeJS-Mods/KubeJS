package dev.latvian.kubejs.block;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.event.EventsJS;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = KubeJS.MOD_ID)
public class KubeJSBlockEventHandler
{
	@SubscribeEvent
	public static void registry(RegistryEvent.Register<Block> event)
	{
		EventsJS.post(KubeJSEvents.BLOCK_REGISTRY, new BlockRegistryEventJS(event.getRegistry()));
	}

	@SubscribeEvent
	public static void rightClick(PlayerInteractEvent.RightClickBlock event)
	{
		if (EventsJS.post(KubeJSEvents.BLOCK_RIGHT_CLICK, new BlockRightClickEventJS(event)))
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void leftClick(PlayerInteractEvent.LeftClickBlock event)
	{
		if (EventsJS.post(KubeJSEvents.BLOCK_LEFT_CLICK, new BlockLeftClickEventJS(event)))
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void blockBreak(BlockEvent.BreakEvent event)
	{
		if (EventsJS.post(KubeJSEvents.BLOCK_BREAK, new BlockBreakEventJS(event)))
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void blockPlace(BlockEvent.PlaceEvent event)
	{
		if (EventsJS.post(KubeJSEvents.BLOCK_PLACE, new BlockPlaceEventJS(event)))
		{
			event.setCanceled(true);
		}
	}
}