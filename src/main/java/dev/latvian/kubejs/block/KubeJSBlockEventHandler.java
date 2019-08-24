package dev.latvian.kubejs.block;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.event.EventsJS;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
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
		EventsJS.INSTANCE.post(KubeJSEvents.BLOCK_REGISTRY, new BlockRegistryEventJS(event.getRegistry()));
	}
}