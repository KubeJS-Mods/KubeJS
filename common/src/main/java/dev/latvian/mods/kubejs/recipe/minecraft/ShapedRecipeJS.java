package dev.latvian.mods.kubejs.recipe.minecraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ShapedRecipeJS extends RecipeJS {
	private final List<String> pattern = new ArrayList<>();
	private final List<String> key = new ArrayList<>();

	@Override
	public void create(ListJS args) {
		if (args.size() < 3) {
			if (args.size() < 2) {
				throw new RecipeExceptionJS("Requires 3 arguments - result, pattern and keys!");
			}

			outputItems.add(parseResultItem(args.get(0)));
			var vertical = ListJS.orSelf(args.get(1));

			if (vertical.isEmpty()) {
				throw new RecipeExceptionJS("Pattern is empty!");
			}

			var id = 0;

			for (var o : vertical) {
				var horizontalPattern = new StringBuilder();
				var horizontal = ListJS.orSelf(o);

				for (var item : horizontal) {
					var ingredient = IngredientJS.of(item);

					if (!ingredient.isEmpty()) {
						var currentId = String.valueOf((char) ('A' + (id++)));
						horizontalPattern.append(currentId);
						inputItems.add(ingredient);
						key.add(currentId);
					} else {
						horizontalPattern.append(" ");
					}
				}

				pattern.add(horizontalPattern.toString());
			}

			var maxLength = pattern.stream().mapToInt(String::length).max().getAsInt();
			var iterator = pattern.listIterator();

			while (iterator.hasNext()) {
				iterator.set(StringUtils.rightPad(iterator.next(), maxLength));
			}

			return;
		}

		outputItems.add(parseResultItem(args.get(0)));

		var pattern1 = ListJS.orSelf(args.get(1));

		if (pattern1.isEmpty()) {
			throw new RecipeExceptionJS("Pattern is empty!");
		}

		List<String> airs = new ArrayList<>(1);

		var key1 = MapJS.of(args.get(2));

		if (key1 == null || key1.isEmpty()) {
			throw new RecipeExceptionJS("Key map is empty!");
		}

		for (var k : key1.keySet()) {
			var o = key1.get(k);

			if (o == ItemStackJS.EMPTY || o.equals("minecraft:air")) {
				airs.add(k);
			} else {
				inputItems.add(parseIngredientItem(o, k));
				key.add(k);
			}
		}

		for (var p : pattern1) {
			var s = String.valueOf(p);

			for (var s1 : airs) {
				s = s.replace(s1, " ");
			}

			pattern.add(s);
		}
	}

	@Override
	public void deserialize() {
		outputItems.add(parseResultItem(json.get("result")));

		for (var e : json.get("pattern").getAsJsonArray()) {
			pattern.add(e.getAsString());
		}

		for (var entry : json.get("key").getAsJsonObject().entrySet()) {
			inputItems.add(parseIngredientItem(entry.getValue(), entry.getKey()));
			key.add(entry.getKey());
		}
	}

	@Override
	public void serialize() {
		if (serializeOutputs) {
			json.add("result", outputItems.get(0).toResultJson());
		}

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

	public ShapedRecipeJS noMirror() {
		json.addProperty("mirror", false);
		save();
		return this;
	}

	public ShapedRecipeJS noShrink() {
		json.addProperty("shrink", false);
		save();
		return this;
	}
}