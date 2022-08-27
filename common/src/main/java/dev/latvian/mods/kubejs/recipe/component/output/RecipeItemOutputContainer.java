package dev.latvian.mods.kubejs.recipe.component.output;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import net.minecraft.world.item.ItemStack;

public class RecipeItemOutputContainer {
	public RecipeJS recipe;
	public ItemStack output;
	private double chance = Double.NaN;

	public JsonElement toJson() {
		if (recipe != null) {
			return recipe.getResultJson(this);
		}

		return output.kjs$toJson();
	}

	public int getCount() {
		return output.getCount();
	}

	public void setCount(int c) {
		output.setCount(c);
	}

	public RecipeItemOutputContainer withCount(int c) {
		var o = new RecipeItemOutputContainer();
		o.recipe = recipe;
		o.output = output.kjs$withCount(c);
		o.chance = chance;
		return this;
	}

	public double getChance() {
		return chance;
	}

	public void setChance(double c) {
		chance = c;
	}

	public RecipeItemOutputContainer withChance(double c) {
		var o = new RecipeItemOutputContainer();
		o.recipe = recipe;
		o.output = output.copy();
		o.chance = c;
		return this;
	}

	public RecipeItemOutputContainer withNoChance() {
		return withChance(Double.NaN);
	}

	public boolean hasChance() {
		return !Double.isNaN(chance);
	}
}
