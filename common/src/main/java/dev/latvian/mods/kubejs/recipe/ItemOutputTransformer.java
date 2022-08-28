package dev.latvian.mods.kubejs.recipe;

import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface ItemOutputTransformer {
	ItemOutputTransformer DEFAULT = (recipe, match, original, with) -> {
		if (original.getCount() != with.getCount()) {
			return with.kjs$withCount(original.getCount());
		}

		return with;
	};

	ItemStack transform(RecipeJS recipe, IngredientMatch match, ItemStack original, ItemStack with);
}
