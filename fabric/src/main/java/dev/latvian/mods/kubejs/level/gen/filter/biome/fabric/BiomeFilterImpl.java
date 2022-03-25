package dev.latvian.mods.kubejs.level.gen.filter.biome.fabric;

import dev.latvian.mods.kubejs.level.gen.filter.biome.BiomeFilter;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;

import java.util.Map;

public class BiomeFilterImpl {
	public static BiomeFilter ofStringAdditional(String s) {
		if (s.charAt(0) == '#') {
			var id = UtilsJS.getMCID(s.substring(1));
			return new BiomeTagFilter(TagKey.create(Registry.BIOME_REGISTRY, id));
		}
		return null;
	}

	public static BiomeFilter ofMapAdditional(Map<String, Object> map) {
		if (map.containsKey("tag")) {
			var tag = UtilsJS.getMCID(map.get("tag").toString());
			return new BiomeTagFilter(TagKey.create(Registry.BIOME_REGISTRY, tag));
		}
		return null;
	}
}
