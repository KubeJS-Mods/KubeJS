package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.RecipeTypeFunction;
import dev.latvian.mods.kubejs.recipe.component.BooleanComponent;
import dev.latvian.mods.kubejs.recipe.component.ComponentValueMap;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.MapRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.util.TinyMap;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public interface ShapedRecipeSchema {
	class ShapedKubeRecipe extends KubeRecipe {
		public KubeRecipe noMirror() {
			return setValue(KJS_MIRROR, false);
		}

		public KubeRecipe noShrink() {
			return setValue(KJS_SHRINK, false);
		}

		private void set2DValues(ComponentValueMap from) {
			setValue(RESULT, from.getValue(this, RESULT));

			var vertical = from.getValue(this, INGREDIENTS);

			if (vertical.length == 0) {
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

			setValue(PATTERN, pattern.toArray(new String[0]));
			setValue(KEY, TinyMap.ofMap(key));
		}

		@Override
		public void afterLoaded() {
			super.afterLoaded();
			var pattern = getValue(PATTERN);
			var key = getValue(KEY);

			if (pattern.length == 0) {
				throw new RecipeExceptionJS("Pattern is empty!");
			}

			if (key.isEmpty()) {
				throw new RecipeExceptionJS("Key map is empty!");
			}

			List<Character> airs = null;

			var entries = new ArrayList<>(Arrays.asList(key.entries()));
			var itr = entries.iterator();

			while (itr.hasNext()) {
				var entry = itr.next();
				if (entry.value() == null || entry.value().isEmpty()) {
					if (airs == null) {
						airs = new ArrayList<>(1);
					}

					airs.add(entry.key());
					itr.remove();
				}
			}

			if (airs != null) {
				for (int i = 0; i < pattern.length; i++) {
					for (var a : airs) {
						pattern[i] = pattern[i].replace(a, ' ');
					}
				}

				setValue(PATTERN, pattern);
				setValue(KEY, new TinyMap<>(entries));
			}
		}

		@Override
		public RecipeTypeFunction getSerializationTypeFunction() {
			// Use vanilla shaped recipe type if KubeJS is not needed
			if (type == type.event.shaped // if this type == kubejs:shaped
				&& type.event.shaped != type.event.vanillaShaped // check if not in serverOnly mode
				&& !json.has("kubejs:actions")
				&& !json.has("kubejs:modify_result")
				&& !json.has("kubejs:stage")
				&& !json.has("kubejs:mirror")
				&& !json.has("kubejs:shrink")
			) {
				return type.event.vanillaShaped;
			}

			return super.getSerializationTypeFunction();
		}
	}

	RecipeKey<OutputItem> RESULT = ItemComponents.OUTPUT.key("result");
	RecipeKey<String[]> PATTERN = StringComponent.NON_EMPTY.asArray().key("pattern");
	RecipeKey<TinyMap<Character, InputItem>> KEY = MapRecipeComponent.ITEM_PATTERN_KEY.key("key");
	RecipeKey<Boolean> KJS_MIRROR = BooleanComponent.BOOLEAN.key("kubejs:mirror").preferred("kjsMirror").optional(true).exclude();
	RecipeKey<Boolean> KJS_SHRINK = BooleanComponent.BOOLEAN.key("kubejs:shrink").preferred("kjsShrink").optional(true).exclude();

	// Used for shaped recipes with 2D ingredient array
	RecipeKey<InputItem[][]> INGREDIENTS = ItemComponents.INPUT_ARRAY.asArray().key("ingredients");

	RecipeSchema SCHEMA = new RecipeSchema(ShapedKubeRecipe.class, ShapedKubeRecipe::new, RESULT, PATTERN, KEY, KJS_MIRROR, KJS_SHRINK)
		.constructor(RESULT, PATTERN, KEY)
		.constructor((recipe, schemaType, keys, from) -> ((ShapedKubeRecipe) recipe).set2DValues(from), RESULT, INGREDIENTS)
		.uniqueOutputId(RESULT);
}
