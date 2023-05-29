package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ComponentValueMap;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.MapRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ShapedRecipeSchema {
	class ShapedRecipeJS extends RecipeJS {
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

		private void set2DValues(ComponentValueMap from) {
			setValue(RESULT, from.getValue(RESULT));

			var vertical = from.getValue(INGREDIENTS);

			if (vertical.isEmpty()) {
				throw new RecipeExceptionJS("Pattern is empty!");
			}

			var pattern = new ArrayList<String>();
			var key = new HashMap<Character, InputItem>();
			var horizontalPattern = new StringBuilder();
			var id = 0;

			for (var horizontal : vertical) {
				for (var ingredient : horizontal) {
					if (!ingredient.isEmpty()) {
						char currentChar = (char) ('A' + (id++));
						horizontalPattern.append(currentChar);
						key.put(currentChar, ingredient);
					} else {
						horizontalPattern.append(' ');
					}
				}

				pattern.add(horizontalPattern.toString());
				horizontalPattern.setLength(0);
			}

			var maxLength = pattern.stream().mapToInt(String::length).max().getAsInt();
			var iterator = pattern.listIterator();

			while (iterator.hasNext()) {
				iterator.set(StringUtils.rightPad(iterator.next(), maxLength));
			}

			setValue(PATTERN, pattern);
			setValue(KEY, key);
		}

		@Override
		public void afterLoaded() {
			var pattern = getValue(PATTERN);
			var key = getValue(KEY);

			if (pattern.isEmpty()) {
				throw new RecipeExceptionJS("Pattern is empty!");
			}

			if (key.isEmpty()) {
				throw new RecipeExceptionJS("Key map is empty!");
			}

			List<Character> airs = null;
			var itr = key.entrySet().iterator();

			while (itr.hasNext()) {
				var entry = itr.next();
				if (entry.getValue().isEmpty()) {
					if (airs == null) {
						airs = new ArrayList<>(1);
					}

					airs.add(entry.getKey());
					itr.remove();
				}
			}

			if (airs != null) {
				for (int i = 0; i < pattern.size(); i++) {
					var s = pattern.get(i);

					for (var a : airs) {
						s = s.replace(a, ' ');
					}

					pattern.set(i, s);
				}

				setValue(PATTERN, pattern);
				setValue(KEY, key);
			}
		}
	}

	RecipeKey<OutputItem> RESULT = ItemComponents.OUTPUT.key(0, "result");
	RecipeKey<List<String>> PATTERN = StringComponent.NON_EMPTY.asArray().key(1, "pattern");
	RecipeKey<Map<Character, InputItem>> KEY = MapRecipeComponent.PATTERN_KEY.key(2, "key");

	// Used for shaped recipes with 2D ingredient array
	RecipeKey<List<List<InputItem>>> INGREDIENTS = ItemComponents.INPUT_ARRAY.asArray().key(-1, "ingredients");

	RecipeSchema SCHEMA = new RecipeSchema(ShapedRecipeJS.class, ShapedRecipeJS::new, RESULT, PATTERN, KEY)
			.constructor(RESULT, PATTERN, KEY)
			.constructor((recipe, schemaType, from) -> ((ShapedRecipeJS) recipe).set2DValues(from), RESULT, INGREDIENTS);
}
