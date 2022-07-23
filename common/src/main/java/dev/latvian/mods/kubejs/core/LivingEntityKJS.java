package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.bindings.ItemWrapper;
import dev.latvian.mods.kubejs.bindings.event.ItemEvents;
import dev.latvian.mods.kubejs.item.FoodEatenEventJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
@RemapPrefixForJS("kjs$")
public interface LivingEntityKJS extends EntityKJS {
	default void kjs$foodEaten(ItemStack is) {
		if (this instanceof ServerPlayer player) {
			var event = new FoodEatenEventJS(player, is);
			var i = is.getItem();

			var b = i.getItemBuilderKJS();
			if (b != null && b.foodBuilder != null && b.foodBuilder.eaten != null) {
				b.foodBuilder.eaten.accept(event);
			}

			ItemEvents.FOOD_EATEN.post(ItemWrapper.getId(i), event);
		}
	}
}