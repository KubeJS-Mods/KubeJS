package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.item.ItemPredicate;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;

// FIXME
public class CompostableRecipesKubeEvent implements KubeEvent {
	public static Object2FloatMap<ItemLike> originalMap = null;

	public CompostableRecipesKubeEvent() {
		if (originalMap == null) {
			originalMap = new Object2FloatOpenHashMap<>(ComposterBlock.COMPOSTABLES);
		} else {
			ComposterBlock.COMPOSTABLES.clear();
			ComposterBlock.COMPOSTABLES.putAll(originalMap);
		}
	}

	public void remove(ItemPredicate match) {
		for (var item : match.kjs$getItemTypes()) {
			ComposterBlock.COMPOSTABLES.removeFloat(item);
		}
	}

	public void removeAll() {
		ComposterBlock.COMPOSTABLES.clear();
	}

	public void add(ItemPredicate match, float f) {
		for (var item : match.kjs$getItemTypes()) {
			ComposterBlock.COMPOSTABLES.put(item, Mth.clamp(f, 0F, 1F));
		}
	}
}