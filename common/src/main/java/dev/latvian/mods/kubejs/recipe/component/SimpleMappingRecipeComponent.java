package dev.latvian.mods.kubejs.recipe.component;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.util.MapJS;

import java.util.Map;
import java.util.Objects;

public class SimpleMappingRecipeComponent<T> extends MappingRecipeComponent<T> {

	@SuppressWarnings("unchecked")
	public SimpleMappingRecipeComponent(RecipeComponent<T> parent, Object mappings) {
		this(HashBiMap.create((Map<String, String>) Objects.requireNonNull(MapJS.of(mappings), "mappings null or invalid map. try using {left: 'right', more: 'mappings'} format")), parent);
	}

	private SimpleMappingRecipeComponent(BiMap<String, String> mappings, RecipeComponent<T> parent) {
		super(parent, o -> to(o, mappings), j -> from(j, mappings.inverse()));
	}

	@Override
	public String componentType() {
		return "simple_mapping";
	}

	@SuppressWarnings("unchecked")
	public static Object to(Object o, Map<String, String> mappings) {
		Map<String, Object> m = (Map<String, Object>) MapJS.of(o); // If Object instanceof Map then if the caller has a reference to it then they will see the mutated map, which may cause issues but hopefully won't
		if (m == null) return o; // we cant deal with it if it's not a map
        mappings.forEach((from, to) -> {
			if (m.containsKey(from)) {
				var value = m.get(from);
				// Doing it in this order means we prevent the map growing
				m.remove(from);
				m.put(to, value);
			}
		});
		return m;
	}

	public static JsonElement from(JsonElement parentOutput, Map<String, String> mappings) {
		if (parentOutput instanceof JsonObject json) {
			// this is the map that backs the json object, so modifying it modifies the json
			Map<String, JsonElement> map = json.asMap();
			mappings.forEach((from, to) -> {
				if (map.containsKey(from)) {
					var value = map.get(from);
					// Doing it in this order means we prevent the map growing
					map.remove(from);
					map.put(to, value);
				}
			});
		}
		return parentOutput;
	}
}
