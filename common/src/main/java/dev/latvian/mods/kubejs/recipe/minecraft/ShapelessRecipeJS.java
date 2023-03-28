package dev.latvian.mods.kubejs.recipe.minecraft;

import com.google.gson.JsonArray;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.IngredientMatch;
import dev.latvian.mods.kubejs.recipe.InputItemTransformer;
import dev.latvian.mods.kubejs.recipe.OutputItemTransformer;
import dev.latvian.mods.kubejs.recipe.RecipeArguments;
import dev.latvian.mods.kubejs.recipe.RecipeJS;

import java.util.List;

/**
 * @author LatvianModder
 */
public class ShapelessRecipeJS extends RecipeJS {
	public OutputItem result;
	public List<InputItem> ingredients;

	@Override
	public void create(RecipeArguments args) {
		result = parseOutputItem(args.get(0));
		ingredients = parseInputItemList(args.get(1));
	}

	@Override
	public void deserialize() {
		result = parseOutputItem(json.get("result"));
		ingredients = parseInputItemList(json.get("ingredients"));
	}

	@Override
	public void serialize() {
		if (serializeInputs) {
			var ingredientsJson = new JsonArray();

			for (var in : ingredients) {
				for (var in1 : in.unwrap()) {
					ingredientsJson.add(inputToJson(in1));
				}
			}

			json.add("ingredients", ingredientsJson);
		}

		if (serializeOutputs) {
			json.add("result", outputToJson(result));
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
	public boolean replaceInput(IngredientMatch match, InputItem with, InputItemTransformer transformer) {
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
	public boolean replaceOutput(IngredientMatch match, OutputItem with, OutputItemTransformer transformer) {
		if (match.contains(result)) {
			result = transformer.transform(this, match, result, with);
			return true;
		}

		return false;
	}
}