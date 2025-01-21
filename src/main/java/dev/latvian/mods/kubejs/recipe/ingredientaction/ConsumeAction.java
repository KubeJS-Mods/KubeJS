package dev.latvian.mods.kubejs.recipe.ingredientaction;

import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;

public class ConsumeAction implements IngredientAction {
	public static final IngredientActionType<ConsumeAction> TYPE = new IngredientActionType<>(KubeJS.id("consume"), MapCodec.unit(new ConsumeAction()));

	@Override
	public IngredientActionType<?> getType() {
		return TYPE;
	}

	@Override
	public ItemStack transform(ItemStack old, int index, CraftingInput input) {
		return ItemStack.EMPTY;
	}
}
