package dev.latvian.mods.kubejs.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public interface ItemMatch extends ReplacementMatch {
	boolean contains(ItemStack item);

	boolean contains(Ingredient in);

	/**
	 * @deprecated This method is fully contained in {@link #contains(ItemLike)}.
	 */
	@Deprecated(forRemoval = true)
	default boolean contains(Block block) {
		var item = block.asItem();
		return item != Items.AIR && contains(item.getDefaultInstance());
	}

	default boolean contains(ItemLike itemLike) {
		var item = itemLike.asItem();
		return item != Items.AIR && contains(item.getDefaultInstance());
	}

	default boolean containsAny(ItemLike... itemLikes) {
		for (var item : itemLikes) {
			if (contains(item)) {
				return true;
			}
		}
		return false;
	}

	default boolean containsAny(Iterable<ItemLike> itemLikes) {
		for (var item : itemLikes) {
			if (contains(item)) {
				return true;
			}
		}
		return false;
	}
}
