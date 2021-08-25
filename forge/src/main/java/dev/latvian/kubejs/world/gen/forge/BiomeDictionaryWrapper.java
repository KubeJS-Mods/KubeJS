package dev.latvian.kubejs.world.gen.forge;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary;

public class BiomeDictionaryWrapper {
	public static BiomeDictionary.Type getBiomeType(Object o) {
		return BiomeDictionary.Type.getType(o.toString());
	}

	public static void addTypes(ResourceLocation[] biomes, BiomeDictionary.Type[] tags) {
		for (ResourceLocation id : biomes) {
			BiomeDictionary.addTypes(ResourceKey.create(Registry.BIOME_REGISTRY, id), tags);
		}
	}
}
