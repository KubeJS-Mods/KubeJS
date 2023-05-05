package dev.latvian.mods.kubejs.recipe.minecraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.IngredientMatch;
import dev.latvian.mods.kubejs.recipe.InputItemTransformer;
import dev.latvian.mods.kubejs.recipe.OutputItemTransformer;
import dev.latvian.mods.kubejs.recipe.RecipeArguments;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class ShapedRecipeJS extends RecipeJS {
	public OutputItem result;
	public final List<String> pattern = new ArrayList<>();
	public final Map<Character, InputItem> key = new LinkedHashMap<>();

	@Override
	public void create(RecipeArguments args) {
		if (args.size() < 3) {
			if (args.size() < 2) {
				throw new RecipeExceptionJS("Requires 3 arguments - result, pattern and keys!");
			}

			result = parseOutputItem(args.get(0));
			var vertical = ListJS.orSelf(args.get(1));

			if (vertical.isEmpty()) {
				throw new RecipeExceptionJS("Pattern is empty!");
			}

			var id = 0;

			for (var o : vertical) {
				var horizontalPattern = new StringBuilder();
				var horizontal = ListJS.orSelf(o);

				for (var item : horizontal) {
					var ingredient = parseInputItem(item);

					if (!ingredient.isEmpty()) {
						char currentChar = (char) ('A' + (id++));
						horizontalPattern.append(currentChar);
						key.put(currentChar, ingredient);
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

		result = parseOutputItem(args.get(0));

		var pattern1 = ListJS.orSelf(args.get(1));

		if (pattern1.isEmpty()) {
			throw new RecipeExceptionJS("Pattern is empty!");
		}

		List<String> airs = new ArrayList<>(1);

		var key1 = MapJS.of(args.get(2));

		if (key1 == null || key1.isEmpty()) {
			throw new RecipeExceptionJS("Key map is empty!");
		}

		for (var kr : key1.keySet()) {
			var k = String.valueOf(kr);
			var o = key1.get(kr);

			if (o == ItemStack.EMPTY || o.equals("minecraft:air")) {
				airs.add(k);
			} else {
				key.put(k.charAt(0), parseInputItem(o, k));
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
		result = parseOutputItem(json.get("result"));

		for (var e : json.get("pattern").getAsJsonArray()) {
			pattern.add(e.getAsString());
		}

		for (var entry : json.get("key").getAsJsonObject().entrySet()) {
			key.put(entry.getKey().charAt(0), parseInputItem(entry.getValue(), entry.getKey()));
		}
	}

	@Override
	public void serialize() {
		if (serializeOutputs) {
			json.add("result", outputToJson(result));
		}

		if (serializeInputs) {
			var patternJson = new JsonArray();

			for (var s : pattern) {
				patternJson.add(s);
			}

			json.add("pattern", patternJson);

			var keyJson = new JsonObject();

			for (var entry : key.entrySet()) {
				keyJson.add(entry.getKey().toString(), inputToJson(entry.getValue()));
			}

			json.add("key", keyJson);
		}
	}

	@Override
	public boolean hasInput(IngredientMatch match) {
		for (var entry : key.entrySet()) {
			if (match.contains(entry.getValue())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean replaceInput(IngredientMatch match, InputItem with, InputItemTransformer transformer) {
		boolean changed = false;

		for (var entry : key.entrySet()) {
			if (match.contains(entry.getValue())) {
				entry.setValue(transformer.transform(this, match, entry.getValue(), with));
				changed = true;
			}
		}

		return changed;
	}

	@Override
	public boolean hasOutput(IngredientMatch match) {
		return match.contains(result);
	}

	@Override
	public boolean replaceOutput(IngredientMatch match, OutputItem with, OutputItemTransformer transformer) {
		if (match.contains(result)) {
			result = transformer.transform(this, match, result, with);
			return true;
		}

		return false;
	}

	public ShapedRecipeJS noMirror() {
		json.addProperty("kubejs:mirror", false);
		save();
		return this;
	}

	public ShapedRecipeJS noShrink() {
		json.addProperty("kubejs:shrink", false);
		save();
		return this;
	}
}