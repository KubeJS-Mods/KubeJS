package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.bindings.RarityWrapper;
import dev.latvian.mods.kubejs.core.ItemKJS;
import dev.latvian.mods.kubejs.core.TieredItemKJS;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class ItemModificationProperties {
	public final ItemKJS item;

	public ItemModificationProperties(ItemKJS i) {
		item = i;
	}

	public void setMaxStackSize(int i) {
		item.setMaxStackSizeKJS(i);
	}

	public void setMaxDamage(int i) {
		item.setMaxDamageKJS(i);
	}

	public void setBurnTime(int i) {
		item.setBurnTimeKJS(i);
	}

	public void setCraftingRemainder(Item i) {
		item.setCraftingRemainderKJS(i);
	}

	public void setFireResistant(boolean b) {
		item.setFireResistantKJS(b);
	}

	public void setRarity(RarityWrapper r) {
		item.setRarityKJS(r.rarity);
	}

	public void setTier(Consumer<ModifiedToolTier> c) {
		if (item instanceof TieredItemKJS) {
			ModifiedToolTier t = new ModifiedToolTier(((TieredItemKJS) item).getTierKJS());
			c.accept(t);
			((TieredItemKJS) item).setTierKJS(t);
		} else {
			throw new IllegalArgumentException("Item is not a tool/tiered item!");
		}
	}

	public void setFoodProperties(Consumer<FoodBuilder> consumer) {
		Item originalItem = (Item) item;
		FoodProperties fp = originalItem.getFoodProperties();
		FoodBuilder builder = fp == null ? new FoodBuilder() : new FoodBuilder(fp);
		consumer.accept(builder);
		item.setFoodPropertiesKJS(builder.build());
	}
}
