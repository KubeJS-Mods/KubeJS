package dev.latvian.mods.kubejs.recipe.match;

import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public interface ItemMatch extends ReplacementMatch {
	boolean matches(RecipeMatchContext cx, ItemStack item, boolean exact);

	boolean matches(RecipeMatchContext cx, Ingredient in, boolean exact);

	default boolean matches(RecipeMatchContext cx, ItemLike itemLike, boolean exact) {
		var item = itemLike.asItem();
		return item != Items.AIR && matches(cx, item.getDefaultInstance(), exact);
	}

	default boolean matchesAny(RecipeMatchContext cx, Iterable<ItemLike> itemLikes, boolean exact) {
		for (var item : itemLikes) {
			if (matches(cx, item, exact)) {
				return true;
			}
		}

		return false;
	}
}
