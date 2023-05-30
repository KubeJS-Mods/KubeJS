package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.ItemMatch;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
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
			return match instanceof ItemMatch m && m.contains(value);
		}

		@Override
		public InputItem replaceInput(RecipeJS recipe, InputItem value, ReplacementMatch match, InputReplacement with) {
			if (match instanceof ItemMatch m && m.contains(value)) {
				return with.replaceInput(recipe, match, value);
			}

			return value;
		}

		@Override
		public String checkEmpty(RecipeKey<InputItem> key, InputItem value) {
			if (value.isEmpty()) {
				return "Ingredient '" + key.name() + "' can't be empty!";
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
			return match instanceof ItemMatch m && m.contains(value);
		}

		@Override
		public OutputItem replaceOutput(RecipeJS recipe, OutputItem value, ReplacementMatch match, OutputReplacement with) {
			if (match instanceof ItemMatch m && m.contains(value)) {
				return with.replaceOutput(recipe, match, value);
			}

			return value;
		}

		@Override
		public String checkEmpty(RecipeKey<OutputItem> key, OutputItem value) {
			if (value.isEmpty()) {
				return "ItemStack '" + key.name() + "' can't be empty!";
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
