package dev.latvian.mods.kubejs.integration.fabric.techreborn;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.item.ingredient.IngredientStack;
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
public class TRRecipeJS extends RecipeJS {
	public List<ItemStack> results;
	public List<Ingredient> ingredients;

	@Override
	public void create(RecipeArguments args) {
		results = parseItemOutputList(args.get(0));
		ingredients = parseItemInputList(args.get(1));
		json.addProperty("power", 2);
		json.addProperty("time", 200);

		if (type.toString().equals("techreborn:blast_furnace")) {
			json.addProperty("heat", 1500);
		}
	}

	@Override
	public void deserialize() {
		results = parseItemOutputList(json.get("results"));
		ingredients = parseItemInputList(json.get("ingredients"));
	}

	public TRRecipeJS power(int i) {
		json.addProperty("power", i);
		save();
		return this;
	}

	public TRRecipeJS time(int i) {
		json.addProperty("time", i);
		save();
		return this;
	}

	public TRRecipeJS heat(int i) {
		json.addProperty("heat", i);
		save();
		return this;
	}

	@Override
	public void serialize() {
		if (serializeOutputs) {
			var array = new JsonArray();

			for (var out : results) {
				array.add(itemToJson(out));
			}

			json.add("results", array);
		}

		if (serializeInputs) {
			var array = new JsonArray();

			for (var in : ingredients) {
				array.add(in.toJson());
			}

			json.add("ingredients", array);
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
		for (var out : results) {
			if (match.contains(out)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean replaceOutput(IngredientMatch match, ItemStack with, ItemOutputTransformer transformer) {
		boolean changed = false;

		for (int i = 0; i < results.size(); i++) {
			var out = results.get(i);

			if (match.contains(out)) {
				results.set(i, transformer.transform(this, match, out, with));
				changed = true;
			}
		}

		return changed;
	}

	@Override
	public JsonElement serializeIngredientStack(IngredientStack in) {
		var o = in.ingredient.toJson().getAsJsonObject();
		o.addProperty("count", in.count);
		return o;
	}
}
