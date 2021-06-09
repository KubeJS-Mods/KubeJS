package dev.latvian.kubejs.world.gen.forge;

import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class WorldgenAddEventJSImpl {

	public static void registerFeature0(ResourceLocation id, ConfiguredFeature<?, ?> feature) {
		BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_FEATURE, id, feature);
	}

}
