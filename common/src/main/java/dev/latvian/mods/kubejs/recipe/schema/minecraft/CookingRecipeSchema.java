package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface CookingRecipeSchema {
	class CookingRecipeJS extends RecipeJS {
		public RecipeJS xp(float xp) {
			return setValue(CookingRecipeSchema.XP, Math.max(0F, xp));
		}

		public RecipeJS cookingTime(int time) {
			return setValue(CookingRecipeSchema.COOKING_TIME, Math.max(0, time));
		}
	}

	RecipeComponent<OutputItem> PLATFORM_OUTPUT_ITEM = new RecipeComponent<>() {
		@Override
		public String componentType() {
			return "output_item";
		}

		@Override
		public JsonObject description() {
			var obj = new JsonObject();
			obj.addProperty("type", componentType());
			obj.addProperty("custom", "string_on_fabric");
			return obj;
		}

		@Override
		public RecipeComponentType getType() {
			return RecipeComponentType.OUTPUT;
		}

		@Override
		public JsonElement write(OutputItem value) {
			if (Platform.isForge()) {
				return RecipeSchema.OUTPUT_ITEM.write(value);
			} else {
				return new JsonPrimitive(value.item.kjs$getId());
			}
		}

		@Override
		public OutputItem read(Object from) {
			return RecipeSchema.OUTPUT_ITEM.read(from);
		}
	};

	RecipeKey<OutputItem> RESULT = PLATFORM_OUTPUT_ITEM.key(0, "result");
	RecipeKey<InputItem> INGREDIENT = RecipeSchema.INPUT_ITEM.key(1, "ingredient");
	RecipeKey<Float> XP = NumberComponent.FLOAT.optional(0F).key(2, "experience");
	RecipeKey<Integer> COOKING_TIME = NumberComponent.INT.optional(200).key(3, "cookingtime");

	RecipeSchema SCHEMA = new RecipeSchema(CookingRecipeJS::new, RESULT, INGREDIENT, XP, COOKING_TIME);
}
