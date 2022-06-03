package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public interface ItemKJS {
	@Nullable
	default ItemBuilder getItemBuilderKJS() {
		throw new NotImplementedException("A mixin should have implemented this method!");
	}

	default void setItemBuilderKJS(ItemBuilder b) {
		throw new NotImplementedException("A mixin should have implemented this method!");
	}

	default CompoundTag getTypeDataKJS() {
		throw new NotImplementedException("A mixin should have implemented this method!");
	}

	default void setMaxStackSizeKJS(int i) {
		throw new NotImplementedException("A mixin should have implemented this method!");
	}

	default void setMaxDamageKJS(int i) {
		throw new NotImplementedException("A mixin should have implemented this method!");
	}

	default void setCraftingRemainderKJS(Item i) {
		throw new NotImplementedException("A mixin should have implemented this method!");
	}

	default void setFireResistantKJS(boolean b) {
		throw new NotImplementedException("A mixin should have implemented this method!");
	}

	default void setRarityKJS(Rarity r) {
		throw new NotImplementedException("A mixin should have implemented this method!");
	}

	default void setBurnTimeKJS(int i) {
		throw new NotImplementedException("A mixin should have implemented this method!");
	}

	default void setFoodPropertiesKJS(FoodProperties properties) {
		throw new NotImplementedException("A mixin should have implemented this method!");
	}
}
