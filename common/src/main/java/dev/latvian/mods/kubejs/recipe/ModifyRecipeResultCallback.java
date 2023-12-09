package dev.latvian.mods.kubejs.recipe;

import com.mojang.serialization.Codec;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface ModifyRecipeResultCallback {
	// FIXME: Implement properly
	Codec<ModifyRecipeResultCallback> CODEC = Codec.unit(null);

	ItemStack modify(ModifyRecipeCraftingGrid grid, ItemStack result);
}
