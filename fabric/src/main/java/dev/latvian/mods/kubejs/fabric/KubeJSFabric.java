package dev.latvian.mods.kubejs.fabric;

import com.mojang.serialization.Lifecycle;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.event.WorldgenEvents;
import dev.latvian.mods.kubejs.item.KubeJSCreativeTabs;
import dev.latvian.mods.kubejs.platform.fabric.IngredientFabricHelper;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;

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

		if (!CommonProperties.get().serverOnly) {
			KubeJSCreativeTabs.init();
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public void registerObjects() {
		var ignored = new HashSet<>(RegistryInfo.AFTER_VANILLA);

		for (var info : RegistryInfo.MAP.values()) {
			final var key = (ResourceKey) info.key;

			if (!ignored.contains(info) && BuiltInRegistries.REGISTRY.get(key) instanceof WritableRegistry<?> reg) {
				info.registerObjects((id, obj) -> reg.register(ResourceKey.create(key, id), UtilsJS.cast(obj.get()), Lifecycle.stable()));
			}
		}

		for (var info : RegistryInfo.AFTER_VANILLA) {
			final var key = (ResourceKey) info.key;

			if (BuiltInRegistries.REGISTRY.get(key) instanceof WritableRegistry<?> reg) {
				info.registerObjects((id, obj) -> reg.register(ResourceKey.create(key, id), UtilsJS.cast(obj.get()), Lifecycle.stable()));
			}
		}
	}

	@Override
	public void onInitializeClient() {
		registerObjects();
		WorldgenEvents.post();
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
		WorldgenEvents.post();
		KubeJS.instance.loadComplete();
	}
}
