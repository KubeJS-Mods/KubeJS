package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public interface ItemKJS {
	@Nullable
	ItemBuilder getItemBuilderKJS();

	void setItemBuilderKJS(ItemBuilder b);

	CompoundTag getTypeDataKJS();

	void setMaxStackSizeKJS(int i);

	void setMaxDamageKJS(int i);

	void setCraftingRemainderKJS(Item i);

	void setFireResistantKJS(boolean b);

	void setRarityKJS(Rarity r);

	void setBurnTimeKJS(int i);

	void setFoodPropertiesKJS(FoodProperties properties);
}
