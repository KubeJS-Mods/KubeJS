package dev.latvian.mods.kubejs.recipe.special;

import dev.latvian.mods.kubejs.plugin.builtin.event.ServerEvents;
import dev.latvian.mods.kubejs.recipe.ModifyCraftingItemKubeEvent;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientActionHolder;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;

import java.util.List;

public interface KubeJSCraftingRecipe extends CraftingRecipe {
	String MIRROR_KEY = "kubejs:mirror";
	String INGREDIENT_ACTIONS_KEY = "kubejs:ingredient_actions";
	String MODIFY_RESULT_KEY = "kubejs:modify_result";

	List<IngredientActionHolder> kjs$getIngredientActions();

	String kjs$getModifyResult();

	default NonNullList<ItemStack> kjs$getRemainingItems(CraftingInput input) {
		var list = NonNullList.withSize(input.size(), ItemStack.EMPTY);

		for (var i = 0; i < list.size(); i++) {
			list.set(i, IngredientAction.getRemaining(input, i, kjs$getIngredientActions()));
		}

		return list;
	}

	default ItemStack kjs$assemble(CraftingInput input, ItemStack baseResult) {
		var modifyResult = kjs$getModifyResult();
		//noinspection ConstantValue
		var result = (baseResult == null || baseResult.isEmpty()) ? ItemStack.EMPTY : baseResult.copy();

		if (!modifyResult.isEmpty()) {
			var event = new ModifyCraftingItemKubeEvent(input, result, 0);
			return (ItemStack) ServerEvents.MODIFY_RECIPE_RESULT.post(ScriptType.SERVER, modifyResult, event).value();
		}

		return result;
	}
}
