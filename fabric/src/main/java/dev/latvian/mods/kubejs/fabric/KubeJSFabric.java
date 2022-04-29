package dev.latvian.mods.kubejs.fabric;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;

public class KubeJSFabric implements ModInitializer, ClientModInitializer, DedicatedServerModInitializer {
	@Override
	public void onInitialize() {
		try {
			KubeJS.instance = new KubeJS();
			KubeJS.instance.setup();
			KubeJSRegistries.init();
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
