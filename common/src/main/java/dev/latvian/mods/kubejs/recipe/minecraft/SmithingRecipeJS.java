package dev.latvian.mods.kubejs.recipe.minecraft;

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
public class SmithingRecipeJS extends RecipeJS {
	public ItemStack result;
	public Ingredient base;
	public Ingredient addition;

	@Override
	public void create(RecipeArguments args) {
		result = parseItemOutput(args.get(0));
		base = parseItemInput(args.get(1));
		addition = parseItemInput(args.get(2));
	}

	@Override
	public void deserialize() {
		result = parseItemOutput(json.get("result"));
		base = parseItemInput(json.get("base"));
		addition = parseItemInput(json.get("addition"));
	}

	@Override
	public void serialize() {
		if (serializeOutputs) {
			json.add("result", itemToJson(result));
		}

		if (serializeInputs) {
			json.add("base", base.toJson());
			json.add("addition", addition.toJson());
		}
	}

	@Override
	public boolean hasInput(IngredientMatch match) {
		return match.contains(base) || match.contains(addition);
	}

	@Override
	public boolean replaceInput(IngredientMatch match, Ingredient with, ItemInputTransformer transformer) {
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
	public boolean replaceOutput(IngredientMatch match, ItemStack with, ItemOutputTransformer transformer) {
		if (match.contains(result)) {
			result = transformer.transform(this, match, result, with);
			return true;
		}

		return false;
	}
}