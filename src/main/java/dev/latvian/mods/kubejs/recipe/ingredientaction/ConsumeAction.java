package dev.latvian.mods.kubejs.recipe.ingredientaction;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

public class ConsumeAction implements IngredientAction {
	public static final IngredientActionType TYPE = new IngredientActionType("consume", MapCodec.unit(new ConsumeAction()));

	@Override
	public IngredientActionType getType() {
		return TYPE;
	}

	@Override
	public ItemStack transform(ItemStack old, int index, CraftingContainer container) {
		return ItemStack.EMPTY;
	}
}
