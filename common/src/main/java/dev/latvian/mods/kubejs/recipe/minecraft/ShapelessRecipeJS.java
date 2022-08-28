package dev.latvian.mods.kubejs.recipe.minecraft;

import com.google.gson.JsonArray;
import dev.latvian.mods.kubejs.recipe.IngredientMatch;
import dev.latvian.mods.kubejs.recipe.ItemInputTransformer;
import dev.latvian.mods.kubejs.recipe.ItemOutputTransformer;
import dev.latvian.mods.kubejs.recipe.RecipeArguments;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

/**
 * @author LatvianModder
 */
public class ShapelessRecipeJS extends RecipeJS {
	public ItemStack result;
	public List<Ingredient> ingredients;

	@Override
	public void create(RecipeArguments args) {
		result = parseItemOutput(args.get(0));
		ingredients = parseItemInputList(args.get(1));
	}

	@Override
	public void deserialize() {
		result = parseItemOutput(json.get("result"));
		ingredients = parseItemInputList(json.get("ingredients"));
	}

	@Override
	public void serialize() {
		if (serializeInputs) {
			var ingredientsJson = new JsonArray();

			for (var in : ingredients) {
				for (var in1 : in.kjs$unwrapStackIngredient()) {
					ingredientsJson.add(in1.toJson());
				}
			}

			json.add("ingredients", ingredientsJson);
		}

		if (serializeOutputs) {
			json.add("result", itemToJson(result));
		}
	}

	@Override
	public boolean hasInput(IngredientMatch match) {
		for (var in : ingredients) {
			if (match.contains(in)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean replaceInput(IngredientMatch match, Ingredient with, ItemInputTransformer transformer) {
		boolean changed = false;

		for (int i = 0; i < ingredients.size(); i++) {
			var in = ingredients.get(i);

			if (match.contains(in)) {
				ingredients.set(i, transformer.transform(this, match, in, with));
				changed = true;
			}
		}

		return changed;
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