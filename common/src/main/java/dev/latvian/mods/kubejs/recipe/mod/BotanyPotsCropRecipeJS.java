package dev.latvian.mods.kubejs.recipe.mod;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.RecipeArguments;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.util.ListJS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class BotanyPotsCropRecipeJS extends RecipeJS {
	public final List<Integer> minRolls = new ArrayList<>();
	public final List<Integer> maxRolls = new ArrayList<>();

	@Override
	public void create(RecipeArguments args) {
		for (var o : ListJS.orSelf(args.get(0))) {
			if (o instanceof Map) {
				var m = (Map<String, Object>) o;
				outputItems.add(parseResultItem(m.get("item")));
				minRolls.add(((Number) m.getOrDefault("minRolls", 1)).intValue());
				maxRolls.add(((Number) m.getOrDefault("maxRolls", 1)).intValue());
			} else {
				outputItems.add(parseResultItem(o));
				minRolls.add(1);
				maxRolls.add(1);
			}
		}

		inputItems.add(parseIngredientItem(args.get(1)));

		var categories = new JsonArray();
		categories.add("grass");
		json.add("categories", categories);
		json.addProperty("growthTicks", 1200);

		var display = new JsonObject();
		display.addProperty("block", inputItems.get(0).getFirst().kjs$getId());
		json.add("display", display);
	}

	@Override
	public void deserialize() {
		inputItems.add(parseIngredientItem(json.get("seed")));

		for (var e : json.get("results").getAsJsonArray()) {
			var o = e.getAsJsonObject();
			var is = parseResultItem(o.get("output"));

			if (o.has("chance")) {
				is.setChance(o.get("chance").getAsDouble());
			}

			outputItems.add(is);
			minRolls.add(o.has("minRolls") ? o.get("minRolls").getAsInt() : 1);
			maxRolls.add(o.has("maxRolls") ? o.get("maxRolls").getAsInt() : 1);
		}
	}

	public BotanyPotsCropRecipeJS growthTicks(int t) {
		json.addProperty("growthTicks", t);
		save();
		return this;
	}

	public BotanyPotsCropRecipeJS categories(String[] c) {
		var categories = new JsonArray();

		for (var s : c) {
			categories.add(s);
		}

		json.add("categories", categories);
		save();
		return this;
	}

	public BotanyPotsCropRecipeJS displayBlock(String b) {
		var display = new JsonObject();
		display.addProperty("block", b);
		json.add("display", display);
		save();
		return this;
	}

	@Override
	public void serialize() {
		if (serializeInputs) {
			json.add("seed", inputItems.get(0).toJson());
		}

		if (serializeOutputs) {
			var array = new JsonArray();

			for (var i = 0; i < outputItems.size(); i++) {
				var is = outputItems.get(i);
				var o = new JsonObject();
				o.addProperty("chance", is.hasChance() ? is.getChance() : 1D);
				o.addProperty("minRolls", minRolls.get(i));
				o.addProperty("maxRolls", maxRolls.get(i));
				is.removeChance();
				o.add("output", is.toResultJson());
				array.add(o);
			}

			json.add("results", array);
		}
	}
}