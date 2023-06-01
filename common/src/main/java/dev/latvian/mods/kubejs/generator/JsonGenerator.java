package dev.latvian.mods.kubejs.generator;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.resources.ResourceLocation;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

public class JsonGenerator {
	private final ConsoleJS console;
	private final Map<ResourceLocation, byte[]> map;

	public JsonGenerator(ConsoleJS c, Map<ResourceLocation, byte[]> m) {
		console = c;
		map = m;
	}

	public void json(ResourceLocation id, JsonElement json) {
		map.put(new ResourceLocation(id.getNamespace(), id.getPath() + ".json"), json.toString().getBytes(StandardCharsets.UTF_8));

		if (console.getDebugEnabled() || console == ConsoleJS.SERVER && DevProperties.get().dataPackOutput) {
			console.info("Generated " + id + ": " + json);
		}
	}

	public Map<ResourceLocation, byte[]> getAllJsons() {
		return Collections.unmodifiableMap(map);
	}
}
