package dev.latvian.mods.kubejs.recipe.special;

import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.recipe.ModifyCraftingItemKubeEvent;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientActionHolder;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface KubeJSCraftingRecipe extends CraftingRecipe {
	String STAGE_KEY = "kubejs:stage";
	String MIRROR_KEY = "kubejs:mirror";
	String INGREDIENT_ACTIONS_KEY = "kubejs:ingredient_actions";
	String MODIFY_RESULT_KEY = "kubejs:modify_result";

	List<IngredientActionHolder> kjs$getIngredientActions();

	String kjs$getModifyResult();

	String kjs$getStage();

	default NonNullList<ItemStack> kjs$getRemainingItems(CraftingInput input) {
		var list = NonNullList.withSize(input.size(), ItemStack.EMPTY);

		for (var i = 0; i < list.size(); i++) {
			list.set(i, IngredientAction.getRemaining(input, i, kjs$getIngredientActions()));
		}

		return list;
	}

	default ItemStack kjs$assemble(CraftingInput input, HolderLookup.Provider registryAccess) {
		if (!kjs$getStage().isEmpty()) {
			/* FIXME
			var player = getPlayer(((CraftingContainerKJS) container).kjs$getMenu());

			if (player == null || !player.kjs$getStages().has(kjs$getStage())) {
				return ItemStack.EMPTY;
			}
			 */
		}

		var modifyResult = kjs$getModifyResult();
		var result = getResultItem(registryAccess);
		//noinspection ConstantValue
		result = (result == null || result.isEmpty()) ? ItemStack.EMPTY : result.copy();

		if (!modifyResult.isEmpty()) {
			var event = new ModifyCraftingItemKubeEvent(input, result, 0);
			return (ItemStack) ServerEvents.MODIFY_RECIPE_RESULT.post(ScriptType.SERVER, modifyResult, event).value();
		}

		return result;
	}

	@Nullable
	private static Player getPlayer(AbstractContainerMenu menu) {
		if (menu instanceof CraftingMenu craft) {
			return craft.player;
		} else if (menu instanceof InventoryMenu inv) {
			return inv.owner;
		} else {
			var server = ServerLifecycleHooks.getCurrentServer();

			if (server != null) {
				for (var player : server.getPlayerList().getPlayers()) {
					// assume that a single menu instance can only be used by a single player
					// (should hold true because menus contain remote state as well)
					if (player.containerMenu == menu && menu.stillValid(player)) {
						return player;
					}
				}
			}
		}

		return null;
	}
}
