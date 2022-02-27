package dev.latvian.mods.kubejs.recipe.mod;

import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.util.JsonIO;
import dev.latvian.mods.kubejs.util.ListJS;

/**
 * @author LatvianModder
 */
public class ArsNouveauEnchantmentRecipeJS extends RecipeJS {
	@Override
	public void create(ListJS args) {
		json.addProperty("enchantment", args.get(0).toString());
		json.addProperty("level", ((Number) args.get(1)).intValue());
		inputItems.addAll(parseIngredientItemList(args.get(2)));

		if (args.size() >= 4) {
			json.addProperty("mana", ((Number) args.get(3)).intValue());
		}
	}

	@Override
	public void deserialize() {
		for (var i = 1; i <= 8; i++) {
			if (json.has("item_" + i)) {
				inputItems.add(parseIngredientItem(json.get("item_" + i)));
			}
		}
	}

	@Override
	public void serialize() {
		if (serializeInputs) {
			for (var i = 1; i < inputItems.size(); i++) {
				json.add("item_" + i, JsonIO.toArray(inputItems.get(i).toJson()));
			}
		}
	}
}