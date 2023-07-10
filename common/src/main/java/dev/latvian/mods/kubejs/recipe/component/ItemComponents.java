package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.ItemMatch;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.util.TinyMap;

import java.util.Map;

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
			return match instanceof ItemMatch m && !value.isEmpty() && m.contains(value.ingredient);
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
			return match instanceof ItemMatch m && !value.isEmpty() && m.contains(value.item);
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

	RecipeComponent<OutputItem> OUTPUT_ID_WITH_COUNT = new RecipeComponentWithParent<>() {
		@Override
		public RecipeComponent<OutputItem> parentComponent() {
			return OUTPUT;
		}

		@Override
		public void writeToJson(RecipeComponentValue<OutputItem> cv, JsonObject json) {
			json.addProperty(cv.key.name, cv.value.item.kjs$getId());
			json.addProperty("count", cv.value.item.getCount());
		}

		@Override
		public void readFromJson(RecipeComponentValue<OutputItem> cv, JsonObject json) {
			RecipeComponentWithParent.super.readFromJson(cv, json);

			if (cv.value != null && json.has("count")) {
				cv.value.item.setCount(json.get("count").getAsInt());
			}
		}

		@Override
		public void readFromMap(RecipeComponentValue<OutputItem> cv, Map<?, ?> map) {
			RecipeComponentWithParent.super.readFromMap(cv, map);

			if (cv.value != null && map.containsKey("count")) {
				cv.value.item.setCount(((Number) map.get("count")).intValue());
			}
		}

		@Override
		public String toString() {
			return parentComponent().toString();
		}
	};
}
