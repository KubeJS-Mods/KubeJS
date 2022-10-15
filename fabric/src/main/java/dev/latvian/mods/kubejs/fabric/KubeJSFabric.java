package dev.latvian.mods.kubejs.fabric;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import dev.latvian.mods.kubejs.platform.fabric.IngredientPlatformHelperImpl;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;

public class KubeJSFabric implements ModInitializer, ClientModInitializer, DedicatedServerModInitializer {
	@Override
	public void onInitialize() {
		try {
			KubeJS.instance = new KubeJS();
			KubeJS.instance.setup();
			RegistryObjectBuilderTypes.MAP.keySet().forEach(KubeJSRegistries::init);
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}

		if (!CommonProperties.get().serverOnly) {
			IngredientPlatformHelperImpl.register();
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
