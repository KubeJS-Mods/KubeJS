package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.EmptyItemError;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ComponentValueMap;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

	RecipeComponent<Map<Character, InputItem>> KEY_COMPONENT = new RecipeComponent<>() {
		@Override
		public String componentType() {
			return "key";
		}

		@Override
		public JsonElement write(Map<Character, InputItem> value) {
			var json = new JsonObject();

			for (var entry : value.entrySet()) {
				json.add(entry.getKey().toString(), ItemComponents.INPUT.write(entry.getValue()));
			}

			return json;
		}

		@Override
		public Map<Character, InputItem> read(Object from) {
			if (from instanceof JsonObject o) {
				var map = new LinkedHashMap<Character, InputItem>(o.size());

				for (var entry : o.entrySet()) {
					var k = StringComponent.CHARACTER.read(entry.getKey());

					try {
						var v = ItemComponents.INPUT.read(entry.getValue());
						map.put(k, v);
					} catch (EmptyItemError ignored) {
						map.put(k, InputItem.EMPTY);
					}
				}

				return map;
			} else if (from instanceof Map<?, ?> m) {
				var map = new LinkedHashMap<Character, InputItem>(m.size());

				for (var entry : m.entrySet()) {
					var k = StringComponent.CHARACTER.read(entry.getKey());

					try {
						var v = ItemComponents.INPUT.read(entry.getValue());
						map.put(k, v);
					} catch (EmptyItemError ignored) {
						map.put(k, InputItem.EMPTY);
					}
				}

				return map;
			} else {
				throw new IllegalArgumentException("Expected JSON object!");
			}
		}

		@Override
		public String toString() {
			return componentType();
		}
	};

	RecipeKey<OutputItem> RESULT = ItemComponents.OUTPUT.key(0, "result");
	RecipeKey<List<String>> PATTERN = StringComponent.NON_EMPTY.asArray().key(1, "pattern");
	RecipeKey<Map<Character, InputItem>> KEY = KEY_COMPONENT.key(2, "key");

	// Used for shaped recipes with 2D ingredient array
	RecipeKey<List<List<InputItem>>> INGREDIENTS = ItemComponents.INPUT_ARRAY.asArray().key(-1, "ingredients");

	RecipeSchema SCHEMA = new RecipeSchema(ShapedRecipeJS.class, ShapedRecipeJS::new, RESULT, PATTERN, KEY)
			.constructor(RESULT, PATTERN, KEY)
			.constructor((recipe, schemaType, from) -> ((ShapedRecipeJS) recipe).set2DValues(from), RESULT, INGREDIENTS);
}
