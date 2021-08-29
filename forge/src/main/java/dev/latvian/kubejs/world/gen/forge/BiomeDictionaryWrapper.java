package dev.latvian.kubejs.world.gen.forge;

import dev.latvian.kubejs.util.ConsoleJS;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
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

	public static void printBiomes(BiomeDictionary.Type type) {
		ConsoleJS.STARTUP.info(type.getName() + ":");

		for (ResourceKey<Biome> biome : BiomeDictionary.getBiomes(type)) {
			ConsoleJS.STARTUP.info("- " + biome.location());
		}
	}

	public static void printTags(ResourceLocation biome) {
		ConsoleJS.STARTUP.info(biome + ":");

		for (BiomeDictionary.Type type : BiomeDictionary.getTypes(ResourceKey.create(Registry.BIOME_REGISTRY, biome))) {
			ConsoleJS.STARTUP.info("- " + type.getName());
		}
	}
}
