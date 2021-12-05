package dev.latvian.mods.kubejs.recipe.mod;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.recipe.minecraft.ShapedRecipeJS;

public class ShapedArtisanRecipeJS extends ShapedRecipeJS {
	private JsonArray getOrCreateArray(String key) {
		JsonArray a = (JsonArray) json.get(key);

		if (a == null) {
			a = new JsonArray();
			json.add(key, a);
		}

		return a;
	}

	public ShapedArtisanRecipeJS tool(IngredientJS ingredient, int damage) {
		if (ingredient.toJson() instanceof JsonObject o) {
			o.addProperty("damage", damage);
			getOrCreateArray("tools").add(o);
		} else {
			JsonObject o = new JsonObject();
			o.addProperty("item", ingredient.getFirst().getId());
			o.addProperty("damage", damage);
			getOrCreateArray("tools").add(o);
		}

		return this;
	}

	public ShapedArtisanRecipeJS fluid(FluidStackJS fluid) {
		JsonObject o = new JsonObject();
		o.addProperty("fluid", fluid.getId());
		o.addProperty("amount", fluid.getAmount());
		json.add("fluidIngredient", o);
		save();
		return this;
	}

	public ShapedArtisanRecipeJS consumeSecondaryIngredients(boolean b) {
		json.addProperty("consumeSecondaryIngredients", b);
		save();
		return this;
	}

	public ShapedArtisanRecipeJS secondaryIngredient(IngredientJS ingredient) {
		getOrCreateArray("secondaryIngredients").add(ingredient.toJson());
		save();
		return this;
	}

	public ShapedArtisanRecipeJS extraOutput(ItemStackJS item) {
		getOrCreateArray("extraOutput").add(item.toResultJson());
		save();
		return this;
	}

	public ShapedArtisanRecipeJS mirrored(boolean b) {
		json.addProperty("mirrored", b);
		save();
		return this;
	}

	public ShapedArtisanRecipeJS minimumTier(int b) {
		json.addProperty("minimumTier", b);
		save();
		return this;
	}

	public ShapedArtisanRecipeJS maximumTier(int b) {
		json.addProperty("maximumTier", b);
		save();
		return this;
	}

	public ShapedArtisanRecipeJS experienceRequired(int b) {
		json.addProperty("experienceRequired", b);
		save();
		return this;
	}

	public ShapedArtisanRecipeJS levelRequired(int b) {
		json.addProperty("levelRequired", b);
		save();
		return this;
	}

	public ShapedArtisanRecipeJS consumeExperience(boolean b) {
		json.addProperty("consumeExperience", b);
		save();
		return this;
	}
}
