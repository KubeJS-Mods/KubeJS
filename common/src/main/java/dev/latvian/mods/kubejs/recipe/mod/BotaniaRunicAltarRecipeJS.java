package dev.latvian.mods.kubejs.recipe.mod;

import com.google.gson.JsonArray;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.util.ListJS;

/**
 * @author LatvianModder
 */
public class BotaniaRunicAltarRecipeJS extends RecipeJS {
	@Override
	public void create(ListJS args) {
		outputItems.add(parseResultItem(args.get(0)));
		inputItems.addAll(parseIngredientItemList(args.get(1)));
		json.addProperty("mana", args.size() >= 3 ? (Number) args.get(2) : 1000);
	}

	@Override
	public void deserialize() {
		outputItems.add(parseResultItem(json.get("output")));
		inputItems.addAll(parseIngredientItemList(json.get("ingredients")));
	}

	public BotaniaRunicAltarRecipeJS mana(int t) {
		json.addProperty("mana", t);
		save();
		return this;
	}

	@Override
	public void serialize() {
		if (serializeOutputs) {
			json.add("output", outputItems.get(0).toResultJson());
		}

		if (serializeInputs) {
			JsonArray a = new JsonArray();

			for (var in : inputItems) {
				a.add(in.toJson());
			}

			json.add("ingredients", a);
		}
	}
}