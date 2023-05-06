package dev.latvian.mods.kubejs.recipe.special;

import dev.latvian.mods.kubejs.platform.RecipePlatformHelper;
import dev.latvian.mods.kubejs.recipe.ModifyRecipeCraftingGrid;
import dev.latvian.mods.kubejs.recipe.ModifyRecipeResultCallback;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientAction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface KubeJSCraftingRecipe extends CraftingRecipe {
	List<IngredientAction> kjs$getIngredientActions();

	@Nullable
	ModifyRecipeResultCallback kjs$getModifyResult();

	String kjs$getStage();

	default NonNullList<ItemStack> kjs$getRemainingItems(CraftingContainer container) {
		var list = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);

		for (var i = 0; i < list.size(); i++) {
			list.set(i, IngredientAction.getRemaining(container, i, kjs$getIngredientActions()));
		}

		return list;
	}

	default ItemStack kjs$assemble(CraftingContainer container) {
		if (!kjs$getStage().isEmpty()) {
			var player = RecipePlatformHelper.get().getCraftingPlayer();

			if (player == null || !player.kjs$getStages().has(kjs$getStage())) {
				return ItemStack.EMPTY;
			}
		}

		if (kjs$getModifyResult() != null) {
			return kjs$getModifyResult().modify(new ModifyRecipeCraftingGrid(container), getResultItem().copy());
		}

		return getResultItem().copy();
	}
}
