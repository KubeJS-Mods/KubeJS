package dev.latvian.kubejs.world.gen.forge;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.world.gen.WorldgenAddEventJS;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = "kubejs")
public class KubeJSForgeWorldgen {

	// TODO: where should this go
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void prepareModifications(BiomeLoadingEvent event) {
		new WorldgenAddEventJS().post(ScriptType.STARTUP, KubeJSEvents.WORLDGEN_ADD);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onBiomesLoad(BiomeLoadingEvent event) {
		// FIXME: find a way to have this occur AFTER every other mod using BiomeModifications
		new WorldgenRemoveEventJSForge(event).post(ScriptType.STARTUP, KubeJSEvents.WORLDGEN_REMOVE);
	}
}
