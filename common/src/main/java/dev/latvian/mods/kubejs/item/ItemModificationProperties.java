package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.core.ItemKJS;
import dev.latvian.mods.kubejs.core.TieredItemKJS;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

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

	public void setRarity(Rarity r) {
		item.setRarityKJS(r);
	}

	public void setTier(Consumer<MutableToolTier> c) {
		if (item instanceof TieredItemKJS kjs) {
			var t = new MutableToolTier(kjs.getTierKJS());
			c.accept(t);
			kjs.setTierKJS(t);
		} else {
			throw new IllegalArgumentException("Item is not a tool/tiered item!");
		}
	}

	public void setFoodProperties(Consumer<FoodBuilder> consumer) {
		var originalItem = (Item) item;
		var fp = originalItem.getFoodProperties();
		var builder = fp == null ? new FoodBuilder() : new FoodBuilder(fp);
		consumer.accept(builder);
		item.setFoodPropertiesKJS(builder.build());
	}
}
