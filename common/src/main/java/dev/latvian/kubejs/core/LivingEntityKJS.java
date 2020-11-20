package dev.latvian.kubejs.core;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.item.ItemFoodEatenEventJS;
import dev.latvian.kubejs.item.ItemJS;
import dev.latvian.kubejs.script.ScriptType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
public interface LivingEntityKJS
{
	default void foodEatenKJS(ItemStack is)
	{
		if (this instanceof ServerPlayer)
		{
			ItemFoodEatenEventJS event = new ItemFoodEatenEventJS((ServerPlayer) this, is);
			Item i = is.getItem();

			if (i instanceof ItemJS)
			{
				ItemJS j = (ItemJS) i;

				if (j.properties.foodBuilder != null && j.properties.foodBuilder.eaten != null)
				{
					j.properties.foodBuilder.eaten.accept(event);
				}
			}

			event.post(ScriptType.SERVER, KubeJSEvents.ITEM_FOOD_EATEN);
		}
	}
}