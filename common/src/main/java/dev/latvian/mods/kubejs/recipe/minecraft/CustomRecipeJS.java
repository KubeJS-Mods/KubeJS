package dev.latvian.mods.kubejs.recipe.minecraft;

import com.google.gson.JsonArray;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.recipe.RecipeArguments;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.special.SpecialRecipeSerializerManager;
import dev.latvian.mods.kubejs.util.ConsoleJS;

/**
 * @author LatvianModder
 */
public class CustomRecipeJS extends RecipeJS {
	private String inputKey;
	private int inputType;
	private String outputKey;
	private int outputType;

	public CustomRecipeJS() {
		inputKey = "";
		inputType = -1;
		outputKey = "";
		outputType = -1;
	}

	@Override
	public void create(RecipeArguments args) {
		throw new RecipeExceptionJS("Can't create custom recipe for type " + getOrCreateId() + "!");
	}

	private boolean addInput(String k) {
		var e = json.get(k);

		if (e == null || e.isJsonNull()) {
			return false;
		}

		if (e.isJsonArray()) {
			for (var e1 : e.getAsJsonArray()) {
				var i = IngredientJS.ingredientFromRecipeJson(e1);

				if (!i.isEmpty()) {
					inputItems.add(i);
				}
			}

			inputKey = k;
			inputType = 1;
			return true;
		}

		var i = IngredientJS.ingredientFromRecipeJson(e);

		if (!i.isEmpty()) {
			inputItems.add(i);
			inputKey = k;
			inputType = 0;
			return true;
		}

		return false;
	}

	private boolean addOutput(String k) {
		var e = json.get(k);

		if (e == null || e.isJsonNull()) {
			return false;
		}

		if (e.isJsonArray()) {
			for (var e1 : e.getAsJsonArray()) {
				var i = ItemStackJS.of(e1);

				if (!i.isEmpty()) {
					outputItems.add(i);
				}
			}

			outputKey = k;
			outputType = 1;
			return true;
		}

		var i = ItemStackJS.of(e);

		if (!i.isEmpty()) {
			if (e.isJsonPrimitive()) {
				outputType = 2;

				if (json.has("count")) {
					i.withCount(json.get("count").getAsInt());
				}
			} else {
				outputType = 0;
			}

			outputKey = k;
			outputItems.add(i);
			return true;
		}

		return false;
	}

	@Override
	public void deserialize() {
		inputItems.clear();
		outputItems.clear();
		inputKey = "";
		inputType = -1;
		outputKey = "";
		outputType = -1;

		if (originalRecipe != null && SpecialRecipeSerializerManager.INSTANCE.isSpecial(originalRecipe)) {
			ConsoleJS.SERVER.debug("Skipped " + this + " as custom recipe because it is dynamic.");
			return;
		}

		try {
			if (!addInput("ingredient")
					&& !addInput("ingredients")
					&& !addInput("in")
					&& !addInput("input")
					&& !addInput("inputs")
					&& !addInput("itemInput")
					&& !addInput("item_input")
					&& !addInput("item_inputs")
					&& !addInput("infusionInput")
			) {
				ConsoleJS.SERVER.debug("! " + this + ": Couldn't find any input items!");
			}
		} catch (Exception ignored) {
		}

		try {
			if (!addOutput("result")
					&& !addOutput("results")
					&& !addOutput("out")
					&& !addOutput("output")
					&& !addOutput("outputs")
					&& !addOutput("itemOutput")
					&& !addOutput("item_output")
					&& !addOutput("item_outputs")
					&& !addOutput("mainOutput")
					&& !addOutput("secondaryOutput")
			) {
				ConsoleJS.SERVER.debug("! " + this + ": Couldn't find any output items!");
			}
		} catch (Exception ignored) {
		}
	}

	@Override
	public void serialize() {
		if (serializeOutputs && outputType != -1 && !outputKey.isEmpty()) {
			if (outputType == 1) {
				var a = new JsonArray();

				for (var in : outputItems) {
					a.add(in.toResultJson());
				}

				json.add(outputKey, a);
			} else if (outputType == 2) {
				json.addProperty(outputKey, (outputItems.isEmpty() ? ItemStackJS.EMPTY : outputItems.get(0)).getId());
				json.addProperty("count", (outputItems.isEmpty() ? ItemStackJS.EMPTY : outputItems.get(0)).getCount());
			} else if (outputType == 0) {
				json.add(outputKey, (outputItems.isEmpty() ? ItemStackJS.EMPTY : outputItems.get(0)).toResultJson());
			}
		}

		if (serializeInputs && inputType != -1 && !inputKey.isEmpty()) {
			if (inputType == 1) {
				var a = new JsonArray();

				for (var in : inputItems) {
					a.add(in.toJson());
				}

				json.add(inputKey, a);
			} else if (inputType == 0) {
				json.add(inputKey, (inputItems.isEmpty() ? ItemStackJS.EMPTY : inputItems.get(0)).toJson());
			}
		}
	}
}