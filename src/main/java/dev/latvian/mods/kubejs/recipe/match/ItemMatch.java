package dev.latvian.mods.kubejs.recipe.match;

import dev.latvian.mods.rhino.Context;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public interface ItemMatch extends ReplacementMatch {
	boolean matches(Context cx, ItemStack item, boolean exact);

	boolean matches(Context cx, Ingredient in, boolean exact);

	/**
	 * @deprecated This method is fully contained in {@link #matches(Context, ItemLike, boolean)}.
	 */
	@Deprecated(forRemoval = true)
	default boolean matches(Context cx, Block block, boolean exact) {
		var item = block.asItem();
		return item != Items.AIR && matches(cx, item.getDefaultInstance(), exact);
	}

	default boolean matches(Context cx, ItemLike itemLike, boolean exact) {
		var item = itemLike.asItem();
		return item != Items.AIR && matches(cx, item.getDefaultInstance(), exact);
	}

	default boolean matchesAny(Context cx, Iterable<ItemLike> itemLikes, boolean exact) {
		for (var item : itemLikes) {
			if (matches(cx, item, exact)) {
				return true;
			}
		}

		return false;
	}
}
