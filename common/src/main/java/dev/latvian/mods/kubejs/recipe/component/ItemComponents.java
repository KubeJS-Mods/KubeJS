package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.InputReplacementTransformer;
import dev.latvian.mods.kubejs.recipe.ItemMatch;
import dev.latvian.mods.kubejs.recipe.OutputReplacementTransformer;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.util.TinyMap;

public interface ItemComponents {
	RecipeComponent<InputItem> INPUT = new RecipeComponent<>() {
		@Override
		public String componentType() {
			return "input_item";
		}

		@Override
		public ComponentRole role() {
			return ComponentRole.INPUT;
		}

		@Override
		public Class<?> componentClass() {
			return InputItem.class;
		}

		@Override
		public boolean hasPriority(RecipeJS recipe, Object from) {
			return recipe.inputItemHasPriority(from);
		}

		@Override
		public JsonElement write(RecipeJS recipe, InputItem value) {
			return recipe.writeInputItem(value);
		}

		@Override
		public InputItem read(RecipeJS recipe, Object from) {
			return recipe.readInputItem(from);
		}

		@Override
		public boolean isInput(RecipeJS recipe, InputItem value, ReplacementMatch match) {
			return match instanceof ItemMatch m && !value.isEmpty() && m.contains(value);
		}

		@Override
		public InputItem replaceInput(RecipeJS recipe, InputItem original, ReplacementMatch match, InputReplacement with) {
			if (match instanceof ItemMatch m && !original.isEmpty() && m.contains(original)) {
				if (with instanceof InputItem withItem) {
					if (original.count != withItem.count) {
						return InputItem.of(withItem.ingredient, original.count);
					}

					return withItem;
				} else if (with instanceof InputReplacementTransformer.Replacement transformer) {
					return read(recipe, transformer.transformer().transform(recipe, match, original, transformer.with()));
				}
			}

			return original;
		}

		@Override
		public String checkEmpty(RecipeKey<InputItem> key, InputItem value) {
			if (value.isEmpty()) {
				return "Ingredient '" + key.name + "' can't be empty!";
			}

			return "";
		}

		@Override
		public RecipeComponent<TinyMap<Character, InputItem>> asPatternKey() {
			return MapRecipeComponent.ITEM_PATTERN_KEY;
		}

		@Override
		public String toString() {
			return componentType();
		}
	};

	RecipeComponent<InputItem[]> INPUT_ARRAY = INPUT.asArray();

	RecipeComponent<InputItem[]> UNWRAPPED_INPUT_ARRAY = new RecipeComponentWithParent<>() {
		@Override
		public RecipeComponent<InputItem[]> parentComponent() {
			return ItemComponents.INPUT_ARRAY;
		}

		@Override
		public JsonElement write(RecipeJS recipe, InputItem[] value) {
			var json = new JsonArray();

			for (var in : value) {
				for (var in1 : in.unwrap()) {
					json.add(ItemComponents.INPUT.write(recipe, in1));
				}
			}

			return json;
		}

		@Override
		public String toString() {
			return parentComponent().toString();
		}
	};

	OutputReplacementTransformer DEFAULT_OUTPUT_TRANSFORMER = (recipe, match, original, with) -> {
		if (original instanceof OutputItem oItem && with instanceof OutputItem wItem) {
			var c = OutputItem.of(wItem.item.copy(), oItem.chance);
			c.item.setCount(oItem.getCount());
			return c;
		}

		return original;
	};

	RecipeComponent<OutputItem> OUTPUT = new RecipeComponent<>() {
		@Override
		public String componentType() {
			return "output_item";
		}

		@Override
		public ComponentRole role() {
			return ComponentRole.OUTPUT;
		}

		@Override
		public Class<?> componentClass() {
			return OutputItem.class;
		}

		@Override
		public boolean hasPriority(RecipeJS recipe, Object from) {
			return recipe.outputItemHasPriority(from);
		}

		@Override
		public JsonElement write(RecipeJS recipe, OutputItem value) {
			return recipe.writeOutputItem(value);
		}

		@Override
		public OutputItem read(RecipeJS recipe, Object from) {
			return recipe.readOutputItem(from);
		}

		@Override
		public boolean isOutput(RecipeJS recipe, OutputItem value, ReplacementMatch match) {
			return match instanceof ItemMatch m && !value.isEmpty() && m.contains(value);
		}

		@Override
		public String checkEmpty(RecipeKey<OutputItem> key, OutputItem value) {
			if (value.isEmpty()) {
				return "ItemStack '" + key.name + "' can't be empty!";
			}

			return "";
		}

		@Override
		public String toString() {
			return componentType();
		}
	};

	RecipeComponent<OutputItem[]> OUTPUT_ARRAY = OUTPUT.asArray();
}
