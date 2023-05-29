package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public interface ItemMatch extends ReplacementMatch {
	boolean contains(ItemStack item);

	boolean contains(Ingredient in);

	default boolean contains(InputItem in) {
		return contains(in.ingredient);
	}

	default boolean contains(OutputItem out) {
		return contains(out.item);
	}

	default boolean contains(Block block) {
		var item = block.asItem();
		return item != Items.AIR && contains(new ItemStack(item));
	}
}
