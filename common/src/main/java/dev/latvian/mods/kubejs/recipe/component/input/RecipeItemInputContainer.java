package dev.latvian.mods.kubejs.recipe.component.input;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import net.minecraft.world.item.crafting.Ingredient;

public class RecipeItemInputContainer {
	public RecipeJS recipe;
	public Ingredient input = null;
	private int count = 1;

	public JsonElement toJson() {
		if (recipe != null) {
			return recipe.getIngredientJson(this);
		}

		return input.toJson();
	}

	public int getCount() {
		return count;
	}

	public void setCount(int c) {
		count = c;
	}

	public RecipeItemInputContainer withCount(int c) {
		setCount(c);
		return this;
	}
}
