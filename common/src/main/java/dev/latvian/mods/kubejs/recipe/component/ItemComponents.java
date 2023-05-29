package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.item.EmptyItemError;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.ItemMatch;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.util.MutableBoolean;

import java.util.List;

public interface ItemComponents {
	RecipeComponent<InputItem> INPUT = new RecipeComponent<>() {
		@Override
		public String componentType() {
			return "input_item";
		}

		@Override
		public RecipeComponentType getType() {
			return RecipeComponentType.INPUT;
		}

		@Override
		public JsonElement write(InputItem value) {
			return value == InputItem.EMPTY ? null : value.ingredient.toJson();
		}

		@Override
		public InputItem read(Object from) {
			var i = InputItem.of(from);

			if (i.isEmpty()) {
				throw new EmptyItemError(from + " is not a valid ingredient!", from);
			}

			return i;
		}

		@Override
		public boolean shouldRead(Object from) {
			return !InputItem.of(from).isEmpty();
		}

		@Override
		public boolean hasInput(RecipeKJS recipe, InputItem value, ReplacementMatch match) {
			return match instanceof ItemMatch m && m.contains(value);
		}

		@Override
		public InputItem replaceInput(RecipeKJS recipe, InputItem value, ReplacementMatch match, InputReplacement with, MutableBoolean changed) {
			if (match instanceof ItemMatch m && m.contains(value)) {
				changed.value = true;
				return with.replaceInput(recipe, match, value);
			}

			return value;
		}

		@Override
		public String toString() {
			return componentType();
		}
	};

	RecipeComponent<InputItem> DEFAULT_INPUT = INPUT.optional(InputItem.EMPTY);
	RecipeComponent<List<InputItem>> INPUT_ARRAY = INPUT.asArray();

	RecipeComponent<List<InputItem>> UNWRAPPED_INPUT_ARRAY = new RecipeComponentWithParent<>() {
		@Override
		public RecipeComponent<List<InputItem>> parentComponent() {
			return ItemComponents.INPUT_ARRAY;
		}

		@Override
		public JsonElement write(List<InputItem> value) {
			var json = new JsonArray();

			for (var in : value) {
				for (var in1 : in.unwrap()) {
					json.add(ItemComponents.INPUT.write(in1));
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
		public RecipeComponentType getType() {
			return RecipeComponentType.OUTPUT;
		}

		@Override
		public JsonElement write(OutputItem value) {
			var json = new JsonObject();
			json.addProperty("item", value.item.kjs$getId());
			json.addProperty("count", value.item.getCount());

			if (value.item.getTag() != null) {
				json.addProperty("nbt", value.item.getTag().toString());
			}

			if (value.hasChance()) {
				json.addProperty("chance", value.getChance());
			}

			return json;
		}

		@Override
		public OutputItem read(Object from) {
			var i = OutputItem.of(from);

			if (i.isEmpty()) {
				throw new EmptyItemError(from + " is not a valid result!", from);
			}

			return i;
		}

		@Override
		public boolean shouldRead(Object from) {
			return !OutputItem.of(from).isEmpty();
		}

		@Override
		public boolean hasOutput(RecipeKJS recipe, OutputItem value, ReplacementMatch match) {
			return match instanceof ItemMatch m && m.contains(value);
		}

		@Override
		public OutputItem replaceOutput(RecipeKJS recipe, OutputItem value, ReplacementMatch match, OutputReplacement with, MutableBoolean changed) {
			if (match instanceof ItemMatch m && m.contains(value)) {
				changed.value = true;
				return with.replaceOutput(recipe, match, value);
			}

			return value;
		}

		@Override
		public String toString() {
			return componentType();
		}
	};

	RecipeComponent<OutputItem> DEFAULT_OUTPUT = OUTPUT.optional(OutputItem.EMPTY);
	RecipeComponent<List<OutputItem>> OUTPUT_ARRAY = OUTPUT.asArray();
}
