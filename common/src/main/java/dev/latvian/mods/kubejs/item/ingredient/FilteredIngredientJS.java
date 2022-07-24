package dev.latvian.mods.kubejs.item.ingredient;

import dev.latvian.mods.kubejs.item.ItemStackSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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
	public boolean test(ItemStack stack) {
		return ingredient.test(stack) && filter.test(stack);
	}

	@Override
	public boolean testItem(Item item) {
		return ingredient.testItem(item) && filter.testItem(item);
	}

	@Override
	public void gatherStacks(ItemStackSet set) {
		for (var stack : ingredient.getStacks()) {
			if (filter.test(stack)) {
				set.add(stack);
			}
		}
	}

	@Override
	public void gatherItemTypes(Set<Item> set) {
		for (var item : ingredient.getItemTypes()) {
			if (filter.testItem(item)) {
				set.add(item);
			}
		}
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