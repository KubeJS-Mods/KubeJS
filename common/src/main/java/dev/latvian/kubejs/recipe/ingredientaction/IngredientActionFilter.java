package dev.latvian.kubejs.recipe.ingredientaction;

import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.util.MapJS;
import net.minecraft.world.item.ItemStack;

public class IngredientActionFilter {
	public static IngredientActionFilter filterOf(Object o) {
		IngredientActionFilter filter = new IngredientActionFilter();

		if (o instanceof Number) {
			filter.filterIndex = ((Number) o).intValue();
		} else if (o instanceof String || o instanceof IngredientJS) {
			filter.filterIngredient = IngredientJS.of(o);

			if (filter.filterIngredient instanceof ItemStackJS) {
				filter.filterIngredient = ((ItemStackJS) filter.filterIngredient).ignoreNBT();
			}
		} else {
			MapJS map = MapJS.of(o);

			if (map != null && !map.isEmpty()) {
				if (map.containsKey("item")) {
					filter.filterIngredient = IngredientJS.of(map.get("item"));
				}

				if (map.containsKey("index")) {
					filter.filterIndex = ((Number) map.get("index")).intValue();
				}
			}
		}

		return filter;
	}

	public int filterIndex = -1;
	public IngredientJS filterIngredient = null;

	public void copyFrom(IngredientActionFilter filter) {
		filterIndex = filter.filterIndex;
		filterIngredient = filter.filterIngredient;
	}

	public boolean checkFilter(int index, ItemStack stack) {
		return (filterIndex == -1 || filterIndex == index) && (filterIngredient == null || filterIngredient.testVanilla(stack));
	}
}
