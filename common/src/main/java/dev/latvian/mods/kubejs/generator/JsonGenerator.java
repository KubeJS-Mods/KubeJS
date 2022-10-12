package dev.latvian.mods.kubejs.generator;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.Map;

public class JsonGenerator {
	private final ConsoleJS console;
	private final Map<ResourceLocation, JsonElement> map;

	public JsonGenerator(ConsoleJS c, Map<ResourceLocation, JsonElement> m) {
		console = c;
		map = m;
	}

	public void json(ResourceLocation id, JsonElement json) {
		map.put(id, json);

		if (console.getDebugEnabled() || console == ConsoleJS.SERVER && DevProperties.get().dataPackOutput) {
			console.info("Generated " + id + ": " + json);
		}
	}

	public Map<ResourceLocation, JsonElement> getAllJsons() {
		return Collections.unmodifiableMap(map);
	}
}
