package dev.latvian.kubejs.core;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.item.ItemFoodEatenEventJS;
import dev.latvian.kubejs.item.ItemJS;
import dev.latvian.kubejs.script.ScriptType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * @author LatvianModder
 */
public interface LivingEntityKJS
{
	default void foodEatenKJS(ItemStack is)
	{
		if (this instanceof ServerPlayerEntity)
		{
			ItemFoodEatenEventJS event = new ItemFoodEatenEventJS((ServerPlayerEntity) this, is);
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