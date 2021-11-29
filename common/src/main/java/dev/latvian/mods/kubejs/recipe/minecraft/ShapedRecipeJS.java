package dev.latvian.mods.kubejs.recipe.minecraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import java.util.ListIterator;
import java.util.Map;

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
			ListJS vertical = ListJS.orSelf(args.get(1));

			if (vertical.isEmpty()) {
				throw new RecipeExceptionJS("Pattern is empty!");
			}

			int id = 0;

			for (var o : vertical) {
				StringBuilder horizontalPattern = new StringBuilder();
				ListJS horizontal = ListJS.orSelf(o);

				for (var item : horizontal) {
					IngredientJS ingredient = IngredientJS.of(item);

					if (!ingredient.isEmpty()) {
						String currentId = String.valueOf((char) ('A' + (id++)));
						horizontalPattern.append(currentId);
						inputItems.add(ingredient);
						key.add(currentId);
					} else {
						horizontalPattern.append(" ");
					}
				}

				pattern.add(horizontalPattern.toString());
			}

			int maxLength = pattern.stream().mapToInt(String::length).max().getAsInt();
			ListIterator<String> iterator = pattern.listIterator();

			while (iterator.hasNext()) {
				iterator.set(StringUtils.rightPad(iterator.next(), maxLength));
			}

			return;
		}

		outputItems.add(parseResultItem(args.get(0)));

		ListJS pattern1 = ListJS.orSelf(args.get(1));

		if (pattern1.isEmpty()) {
			throw new RecipeExceptionJS("Pattern is empty!");
		}

		List<String> airs = new ArrayList<>(1);

		MapJS key1 = MapJS.of(args.get(2));

		if (key1 == null || key1.isEmpty()) {
			throw new RecipeExceptionJS("Key map is empty!");
		}

		for (var k : key1.keySet()) {
			Object o = key1.get(k);

			if (o == ItemStackJS.EMPTY || o.equals("minecraft:air")) {
				airs.add(k);
			} else {
				inputItems.add(parseIngredientItem(o, k));
				key.add(k);
			}
		}

		for (var p : pattern1) {
			String s = String.valueOf(p);

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

		for (Map.Entry<String, JsonElement> entry : json.get("key").getAsJsonObject().entrySet()) {
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
			JsonArray patternJson = new JsonArray();

			for (var s : pattern) {
				patternJson.add(s);
			}

			json.add("pattern", patternJson);

			JsonObject keyJson = new JsonObject();

			for (int i = 0; i < key.size(); i++) {
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