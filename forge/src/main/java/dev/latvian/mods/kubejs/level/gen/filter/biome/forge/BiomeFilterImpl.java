package dev.latvian.mods.kubejs.level.gen.filter.biome.forge;

import dev.latvian.mods.kubejs.level.gen.filter.biome.BiomeFilter;
import dev.latvian.mods.kubejs.level.gen.forge.BiomeDictionaryWrapper;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ConsoleJS;

import java.io.Console;
import java.util.Map;

public class BiomeFilterImpl {
	public static BiomeFilter ofStringAdditional(String s) {
		return switch (s.charAt(0)) {
			case '#' -> {
				ConsoleJS.STARTUP.error("Biome Tag filters are currently not supported on Forge!");
				// TODO: Biome Tags (needs MinecraftForge/MinecraftForge#8251?)
				yield null;
			}
			case '$' -> new BiomeDictionaryFilter(BiomeDictionaryWrapper.getBiomeType(s.substring(1)));
			default -> null;
		};
	}

	public static BiomeFilter ofMapAdditional(Map<String, Object> map) {
		if (map.containsKey("biome_type")) {
			var type = BiomeDictionaryWrapper.getBiomeType(map.get("biome_type"));
			return new BiomeDictionaryFilter(type);
		}
		return null;
	}

}
