package dev.latvian.mods.kubejs.recipe.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.recipe.IngredientMatch;
import dev.latvian.mods.kubejs.recipe.ItemInputTransformer;
import dev.latvian.mods.kubejs.recipe.ItemOutputTransformer;
import dev.latvian.mods.kubejs.recipe.RecipeArguments;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * @author LatvianModder
 */
public class CookingRecipeJS extends RecipeJS {
	public ItemStack result;
	public Ingredient ingredient;

	@Override
	public void create(RecipeArguments args) {
		result = parseItemOutput(args.get(0));
		ingredient = parseItemInput(args.get(1));

		if (args.size() >= 3) {
			xp(args.getFloat(2, 0F));
		}

		if (args.size() >= 4) {
			cookingTime(args.getInt(3, 200));
		}
	}

	public CookingRecipeJS xp(float xp) {
		json.addProperty("experience", Math.max(0F, xp));
		save();
		return this;
	}

	public CookingRecipeJS cookingTime(int time) {
		json.addProperty("cookingtime", Math.max(0, time));
		save();
		return this;
	}

	@Override
	public void deserialize() {
		result = parseItemOutput(json.get("result"));
		ingredient = parseItemInput(json.get("ingredient"));
	}

	@Override
	public void serialize() {
		if (serializeOutputs) {
			json.add("result", itemToJson(result));
		}

		if (serializeInputs) {
			json.add("ingredient", ingredient.toJson());
		}
	}

	@Override
	public JsonElement itemToJson(ItemStack stack) {
		if (Platform.isForge()) {
			return super.itemToJson(result);
		} else {
			return new JsonPrimitive(result.kjs$getId());
		}
	}

	@Override
	public boolean hasInput(IngredientMatch match) {
		return match.contains(ingredient);
	}

	@Override
	public boolean replaceInput(IngredientMatch match, Ingredient with, ItemInputTransformer transformer) {
		if (match.contains(with)) {
			ingredient = transformer.transform(this, match, ingredient, with);
			return true;
		}

		return false;
	}

	@Override
	public boolean hasOutput(IngredientMatch match) {
		return match.contains(result);
	}

	@Override
	public boolean replaceOutput(IngredientMatch match, ItemStack with, ItemOutputTransformer transformer) {
		if (match.contains(result)) {
			result = transformer.transform(this, match, result, with);
			return true;
		}

		return false;
	}
}