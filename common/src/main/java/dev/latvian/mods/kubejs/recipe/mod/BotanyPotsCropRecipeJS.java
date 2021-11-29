package dev.latvian.mods.kubejs.recipe.mod;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.ItemStackJS;
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
	public void create(ListJS args) {
		for (Object o : ListJS.orSelf(args.get(0))) {
			if (o instanceof Map) {
				Map<String, Object> m = (Map<String, Object>) o;
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

		JsonArray categories = new JsonArray();
		categories.add("grass");
		json.add("categories", categories);
		json.addProperty("growthTicks", 1200);

		JsonObject display = new JsonObject();
		display.addProperty("block", inputItems.get(0).getFirst().getId());
		json.add("display", display);
	}

	@Override
	public void deserialize() {
		inputItems.add(parseIngredientItem(json.get("seed")));

		for (JsonElement e : json.get("results").getAsJsonArray()) {
			JsonObject o = e.getAsJsonObject();
			ItemStackJS is = parseResultItem(o.get("output"));

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
		JsonArray categories = new JsonArray();

		for (String s : c) {
			categories.add(s);
		}

		json.add("categories", categories);
		save();
		return this;
	}

	public BotanyPotsCropRecipeJS displayBlock(String b) {
		JsonObject display = new JsonObject();
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
			JsonArray array = new JsonArray();

			for (int i = 0; i < outputItems.size(); i++) {
				ItemStackJS is = outputItems.get(i);
				JsonObject o = new JsonObject();
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