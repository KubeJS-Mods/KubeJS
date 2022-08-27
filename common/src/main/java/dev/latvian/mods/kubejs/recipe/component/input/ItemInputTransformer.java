package dev.latvian.mods.kubejs.recipe.component.input;

import dev.latvian.mods.kubejs.recipe.RecipeJS;
import net.minecraft.world.item.crafting.Ingredient;

@FunctionalInterface
public interface ItemInputTransformer {
	ItemInputTransformer DEFAULT = (recipe, in, original) -> in;

	Ingredient transform(RecipeJS recipe, Ingredient in, Ingredient original);
}
