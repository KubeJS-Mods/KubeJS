package dev.latvian.mods.kubejs.recipe.minecraft;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.IngredientMatch;
import dev.latvian.mods.kubejs.recipe.InputItemTransformer;
import dev.latvian.mods.kubejs.recipe.OutputItemTransformer;
import dev.latvian.mods.kubejs.recipe.RecipeArguments;
import dev.latvian.mods.kubejs.recipe.RecipeJS;

/**
 * @author LatvianModder
 */
public class SmithingRecipeJS extends RecipeJS {
	public OutputItem result;
	public InputItem base;
	public InputItem addition;

	@Override
	public void create(RecipeArguments args) {
		result = parseOutputItem(args.get(0));
		base = parseInputItem(args.get(1));
		addition = parseInputItem(args.get(2));
	}

	@Override
	public void deserialize() {
		result = parseOutputItem(json.get("result"));
		base = parseInputItem(json.get("base"));
		addition = parseInputItem(json.get("addition"));
	}

	@Override
	public void serialize() {
		if (serializeOutputs) {
			json.add("result", outputToJson(result));
		}

		if (serializeInputs) {
			json.add("base", inputToJson(base));
			json.add("addition", inputToJson(addition));
		}
	}

	@Override
	public boolean hasInput(IngredientMatch match) {
		return match.contains(base) || match.contains(addition);
	}

	@Override
	public boolean replaceInput(IngredientMatch match, InputItem with, InputItemTransformer transformer) {
		boolean changed = false;

		if (match.contains(base)) {
			base = transformer.transform(this, match, base, with);
			changed = true;
		}

		if (match.contains(addition)) {
			addition = transformer.transform(this, match, addition, with);
			changed = true;
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