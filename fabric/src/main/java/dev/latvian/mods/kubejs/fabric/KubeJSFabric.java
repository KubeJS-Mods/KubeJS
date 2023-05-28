package dev.latvian.mods.kubejs.fabric;

import dev.architectury.registry.registries.DeferredRegister;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.platform.fabric.IngredientFabricHelper;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;

import java.util.HashSet;

public class KubeJSFabric implements ModInitializer, ClientModInitializer, DedicatedServerModInitializer {
	@Override
	public void onInitialize() {
		try {
			KubeJS.instance = new KubeJS();
			KubeJS.instance.setup();
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}

		IngredientFabricHelper.register();
	}

	public void registerObjects() {
		var ignored = new HashSet<>(RegistryInfo.AFTER_VANILLA);

		for (var info : RegistryInfo.MAP.values()) {
			if (!ignored.contains(info)) {
				var deferredRegister = DeferredRegister.create(KubeJS.MOD_ID, UtilsJS.cast(info.key));
				int added = info.registerObjects(deferredRegister::register);

				if (added > 0) {
					deferredRegister.register();
				}
			}
		}

		for (var info : RegistryInfo.AFTER_VANILLA) {
			var deferredRegister = DeferredRegister.create(KubeJS.MOD_ID, UtilsJS.cast(info.key));
			int added = info.registerObjects(deferredRegister::register);

			if (added > 0) {
				deferredRegister.register();
			}
		}
	}

	@Override
	public void onInitializeClient() {
		registerObjects();
		KubeJS.instance.loadComplete();
		KubeJS.PROXY.clientSetup();
		clientRegistry();
	}

	private void clientRegistry() {
		KubeJSFabricClient.registry();
	}

	@Override
	public void onInitializeServer() {
		registerObjects();
		KubeJS.instance.loadComplete();
	}
}
