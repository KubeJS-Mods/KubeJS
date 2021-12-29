package dev.latvian.kubejs.recipe;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;

/**
 * @author LatvianModder
 */
public class CompostablesRecipeEventJS extends EventJS {

	public static Object2FloatMap<ItemLike> originalMap = null;

	public CompostablesRecipeEventJS() {
		if (originalMap == null) {
			originalMap = new Object2FloatOpenHashMap<>(ComposterBlock.COMPOSTABLES);
		} else {
			ComposterBlock.COMPOSTABLES.clear();
			ComposterBlock.COMPOSTABLES.putAll(originalMap);
		}
	}

	public void remove(IngredientJS o) {
		for (Item item : o.getVanillaItems()) {
			ComposterBlock.COMPOSTABLES.removeFloat(item);
		}
	}

	public void removeAll() {
		ComposterBlock.COMPOSTABLES.clear();
	}

	public void add(IngredientJS o, float f) {
		for (Item item : o.getVanillaItems()) {
			ComposterBlock.COMPOSTABLES.put(item, Mth.clamp(f, 0F, 1F));
		}
	}
}