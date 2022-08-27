package dev.latvian.mods.kubejs.recipe.component.output;

import dev.latvian.mods.kubejs.recipe.RecipeJS;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface ItemOutputTransformer {
	ItemOutputTransformer DEFAULT = (recipe, out, original) -> out;

	ItemStack transform(RecipeJS recipe, ItemStack out, ItemStack original);
}
