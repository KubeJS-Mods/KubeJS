package dev.latvian.mods.kubejs.level.gen.filter.biome.fabric;

import dev.latvian.mods.kubejs.level.gen.filter.biome.BiomeFilter;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.fabricmc.fabric.api.tag.TagFactory;

import java.util.Map;

public class BiomeFilterImpl {
	public static BiomeFilter ofStringAdditional(String s) {
		if (s.charAt(0) == '#') {
			var id = UtilsJS.getMCID(s.substring(1));
			return new BiomeTagFilter(TagFactory.BIOME.create(id));
		}
		return null;
	}

	public static BiomeFilter ofMapAdditional(Map<String, Object> map) {
		if (map.containsKey("tag")) {
			var tag = UtilsJS.getMCID(map.get("tag").toString());
			return new BiomeTagFilter(TagFactory.BIOME.create(tag));
		}
		return null;
	}
}
