package dev.latvian.kubejs.world.gen.fabric;

import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.HashMap;
import java.util.Map;

public class WorldgenAddEventJSImpl {

	public static Map<ResourceLocation, ConfiguredFeature<?, ?>> features = new HashMap<>();

	public static void registerFeature0(ResourceLocation id, ConfiguredFeature<?, ?> feature) {
		features.put(id, feature);
	}

	static {
		DynamicRegistrySetupCallback.EVENT.register(registryAccess -> {
			WritableRegistry<ConfiguredFeature<?, ?>> reg = registryAccess.registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY);
			features.forEach((id, feature) -> Registry.register(reg, id, feature));
		});
	}
}
