package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;

/**
 * @author LatvianModder
 */
public class CompostableRecipesEventJS extends EventJS {
	public static final EventHandler EVENT = EventHandler.server(CompostableRecipesEventJS.class).legacy("recipes.compostables");

	public static Object2FloatMap<ItemLike> originalMap = null;

	public CompostableRecipesEventJS() {
		if (originalMap == null) {
			originalMap = new Object2FloatOpenHashMap<>(ComposterBlock.COMPOSTABLES);
		} else {
			ComposterBlock.COMPOSTABLES.clear();
			ComposterBlock.COMPOSTABLES.putAll(originalMap);
		}
	}

	public void remove(IngredientJS ingredient) {
		for (var item : ingredient.getVanillaItems()) {
			ComposterBlock.COMPOSTABLES.removeFloat(item);
		}
	}

	public void removeAll() {
		ComposterBlock.COMPOSTABLES.clear();
	}

	public void add(IngredientJS ingredient, float f) {
		for (var item : ingredient.getVanillaItems()) {
			ComposterBlock.COMPOSTABLES.put(item, Mth.clamp(f, 0F, 1F));
		}
	}
}