package dev.latvian.mods.kubejs.recipe.mod;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.util.ListJS;

/**
 * @author LatvianModder
 */
public class MalumSpiritFocusingRecipeJS extends RecipeJS {
	@Override
	public void create(ListJS args) {
		outputItems.add(parseResultItem(args.get(0)));
		inputItems.add(parseIngredientItem(args.get(1)));
		inputItems.addAll(parseIngredientItemList(args.get(2)));
		json.addProperty("time", 1200);
		json.addProperty("durabilityCost", 3);
	}

	public MalumSpiritFocusingRecipeJS time(int time) {
		json.addProperty("time", time);
		save();
		return this;
	}

	public MalumSpiritFocusingRecipeJS durabilityCost(int durabilityCost) {
		json.addProperty("durabilityCost", durabilityCost);
		save();
		return this;
	}

	@Override
	public void deserialize() {
		outputItems.add(parseResultItem(json.get("output")));
		inputItems.add(parseIngredientItem("input"));
		inputItems.addAll(parseIngredientItemList(json.get("spirits")));
	}

	@Override
	public void serialize() {
		if (serializeOutputs) {
			json.add("output", outputItems.get(0).toResultJson());
		}

		if (serializeInputs) {
			json.add("input", inputItems.get(0).toJson());

			JsonArray spirits = new JsonArray();

			for (var i = 1; i < inputItems.size(); i++) {
				ItemStackJS stack = inputItems.get(i).getFirst();
				JsonObject json1 = new JsonObject();
				json1.addProperty("item", stack.getId());
				json1.addProperty("count", stack.getCount());
				spirits.add(json1);
			}

			json.add("spirits", spirits);
		}
	}
}