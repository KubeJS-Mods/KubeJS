package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.item.ItemStackSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import org.jetbrains.annotations.ApiStatus;

import java.util.stream.Collector;
import java.util.stream.Stream;

public interface CustomIngredientKJS extends ItemPredicate {
	default Stream<ItemStack> getItems() {
		throw new NoMixinException();
	}

	default boolean kjs$canBeUsedForMatching() {
		return false;
	}

	@ApiStatus.NonExtendable
	default Ingredient kjs$asIngredient() {
		return ((ICustomIngredient) this).toVanilla();
	}

	@ApiStatus.NonExtendable
	default ItemStack[] kjs$getStackArray() {
		return getItems().toArray(ItemStack[]::new);
	}

	default ItemStackSet kjs$getDisplayStacks() {
		return getItems().collect(Collector.of(ItemStackSet::new, ItemStackSet::add, ItemStackSet::merge));
	}
}
