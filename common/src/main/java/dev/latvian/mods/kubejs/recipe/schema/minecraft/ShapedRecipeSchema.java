package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ComponentValueMap;
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
		public void afterLoaded(boolean created) {
			// FIXME: Cleanup empty keys

			/*
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
		 */
		}
	}

	RecipeKey<OutputItem> RESULT = RecipeSchema.OUTPUT_ITEM.key(0, "result");
	RecipeKey<List<String>> PATTERN = StringComponent.NON_EMPTY.asArray().key(1, "pattern");
	RecipeKey<Map<Character, InputItem>> KEY = RecipeSchema.INPUT_ITEM.asPatternKey().key(2, "key");

	// Used for shaped recipes with 2D ingredient array
	RecipeKey<List<List<InputItem>>> INGREDIENTS = RecipeSchema.INPUT_ITEM_ARRAY.asArray().key(-1, "ingredients");

	RecipeSchema SCHEMA = new RecipeSchema(ShapedRecipeJS::new, RESULT, PATTERN, KEY)
			.constructor(RESULT, PATTERN, KEY)
			.constructor((recipe, schemaType, from) -> ((ShapedRecipeJS) recipe).set2DValues(from), RESULT, INGREDIENTS);
}
