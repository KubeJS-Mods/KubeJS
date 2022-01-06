package dev.latvian.mods.kubejs.fabric;

import dev.latvian.mods.kubejs.KubeJS;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;

public class KubeJSFabric implements ModInitializer, ClientModInitializer, DedicatedServerModInitializer {
	@Override
	public void onInitialize() {
		try {
			KubeJS.instance = new KubeJS();
			KubeJS.instance.setup();

			//BiomeModifications.create(new ResourceLocation("kubejs", "worldgen_removals")).add(ModificationPhase.REMOVALS, BiomeSelectors.all(), (s, m) -> new WorldgenRemoveEventJSFabric(s, m).post(ScriptType.STARTUP, KubeJSEvents.WORLDGEN_REMOVE));
			//BiomeModifications.create(new ResourceLocation("kubejs", "worldgen_additions")).add(ModificationPhase.REPLACEMENTS, BiomeSelectors.all(), (s, m) -> new WorldgenAddEventJSFabric(s, m).post(ScriptType.STARTUP, KubeJSEvents.WORLDGEN_ADD));
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	@Override
	public void onInitializeClient() {
		KubeJS.instance.loadComplete();
	}

	@Override
	public void onInitializeServer() {
		KubeJS.instance.loadComplete();
	}
}
