package dev.latvian.mods.kubejs.item.ingredient;

import dev.latvian.mods.kubejs.item.ItemStackJS;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author LatvianModder
 */
public final class FilteredIngredientJS implements IngredientJS {
	private final IngredientJS ingredient;
	private final IngredientJS filter;

	public FilteredIngredientJS(IngredientJS i, IngredientJS f) {
		ingredient = i;
		filter = f;
	}

	@Override
	public boolean test(ItemStackJS stack) {
		return ingredient.test(stack) && filter.test(stack);
	}

	@Override
	public boolean testVanilla(ItemStack stack) {
		return ingredient.testVanilla(stack) && filter.testVanilla(stack);
	}

	@Override
	public boolean testVanillaItem(Item item) {
		return ingredient.testVanillaItem(item) && filter.testVanillaItem(item);
	}

	@Override
	public Set<ItemStackJS> getStacks() {
		Set<ItemStackJS> set = new LinkedHashSet<>();

		for (ItemStackJS stack : ingredient.getStacks()) {
			if (filter.test(stack)) {
				set.add(stack);
			}
		}

		return set;
	}

	@Override
	public Set<Item> getVanillaItems() {
		Set<Item> set = new LinkedHashSet<>();

		for (Item item : ingredient.getVanillaItems()) {
			if (filter.testVanillaItem(item)) {
				set.add(item);
			}
		}

		return set;
	}

	@Override
	public IngredientJS copy() {
		return new FilteredIngredientJS(ingredient.copy(), filter.copy());
	}

	@Override
	public String toString() {
		return "Ingredient.of(" + ingredient + ").filter(" + filter + ")";
	}
}