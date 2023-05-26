package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.event.EventJS;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.util.Mth;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;

public class CompostableRecipesEventJS extends EventJS {
	public static Object2FloatMap<ItemLike> originalMap = null;

	public CompostableRecipesEventJS() {
		if (originalMap == null) {
			originalMap = new Object2FloatOpenHashMap<>(ComposterBlock.COMPOSTABLES);
		} else {
			ComposterBlock.COMPOSTABLES.clear();
			ComposterBlock.COMPOSTABLES.putAll(originalMap);
		}
	}

	public void remove(Ingredient ingredient) {
		for (var item : ingredient.kjs$getItemTypes()) {
			ComposterBlock.COMPOSTABLES.removeFloat(item);
		}
	}

	public void removeAll() {
		ComposterBlock.COMPOSTABLES.clear();
	}

	public void add(Ingredient ingredient, float f) {
		for (var item : ingredient.kjs$getItemTypes()) {
			ComposterBlock.COMPOSTABLES.put(item, Mth.clamp(f, 0F, 1F));
		}
	}
}