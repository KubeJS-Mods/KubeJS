package dev.latvian.mods.kubejs.recipe.mod;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.util.ListJS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class MATagRecipeJS extends RecipeJS {
	private final List<String> pattern = new ArrayList<>();
	private final List<String> key = new ArrayList<>();

	@Override
	public void create(ListJS args) {
		throw new RecipeExceptionJS("Can't create recipe with this type! Use regular shaped crafting");
	}

	@Override
	public void deserialize() {
		for (var e : json.get("pattern").getAsJsonArray()) {
			pattern.add(e.getAsString());
		}

		if (pattern.isEmpty()) {
			throw new RecipeExceptionJS("Pattern is empty!");
		}

		for (var entry : json.get("key").getAsJsonObject().entrySet()) {
			inputItems.add(parseIngredientItem(entry.getValue(), entry.getKey()));
			key.add(entry.getKey());
		}

		if (key.isEmpty()) {
			throw new RecipeExceptionJS("Key map is empty!");
		}
	}

	@Override
	public void serialize() {
		if (serializeInputs) {
			var patternJson = new JsonArray();

			for (var s : pattern) {
				patternJson.add(s);
			}

			json.add("pattern", patternJson);

			var keyJson = new JsonObject();

			for (var i = 0; i < key.size(); i++) {
				keyJson.add(key.get(i), inputItems.get(i).toJson());
			}

			json.add("key", keyJson);
		}
	}
}