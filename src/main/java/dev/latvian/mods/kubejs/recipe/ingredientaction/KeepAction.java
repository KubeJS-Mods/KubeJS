package dev.latvian.mods.kubejs.recipe.ingredientaction;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;

public class KeepAction implements IngredientAction {
	public static final IngredientActionType<KeepAction> TYPE = new IngredientActionType<>("keep", MapCodec.unit(new KeepAction()));

	@Override
	public IngredientActionType<?> getType() {
		return TYPE;
	}

	@Override
	public ItemStack transform(ItemStack old, int index, CraftingInput input) {
		old.setCount(1);
		return old;
	}
}
