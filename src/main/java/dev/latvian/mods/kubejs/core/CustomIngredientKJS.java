package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.item.ItemStackSet;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.ApiStatus;

import java.util.stream.Stream;

public interface CustomIngredientKJS extends ItemPredicate {
	Ingredient toVanilla();

	Stream<Holder<Item>> getItems();

	default boolean kjs$canBeUsedForMatching() {
		return false;
	}

	@ApiStatus.NonExtendable
	default Ingredient kjs$asIngredient() {
		return this.toVanilla();
	}

	@ApiStatus.NonExtendable
	default ItemStack[] kjs$getStackArray() {
		return getItems()
			.map(ItemStackTemplate::new)
			.map(ItemStackTemplate::create)
			.toArray(ItemStack[]::new);
	}

	default ItemStackSet kjs$getDisplayStacks() {
		var set = new ItemStackSet();

		for (var template : ItemWrapper.getList()) {
			var stack = template.create();
			if (test(stack)) {
				set.add(stack);
			}
		}

		return set;
	}
}
