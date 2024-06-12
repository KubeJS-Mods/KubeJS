package dev.latvian.mods.kubejs.recipe.special;

import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.core.CraftingContainerKJS;
import dev.latvian.mods.kubejs.recipe.ContainerModifyRecipeCraftingGrid;
import dev.latvian.mods.kubejs.recipe.ModifyRecipeResultKubeEvent;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientActionHolder;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface KubeJSCraftingRecipe extends CraftingRecipe {
	String STAGE_KEY = "kubejs:stage";
	String SHRINK_KEY = "kubejs:shrink";
	String MIRROR_KEY = "kubejs:mirror";
	String INGREDIENT_ACTIONS_KEY = "kubejs:ingredient_actions";
	String MODIFY_RESULT_KEY = "kubejs:modify_result";

	List<IngredientActionHolder> kjs$getIngredientActions();

	String kjs$getModifyResult();

	String kjs$getStage();

	default NonNullList<ItemStack> kjs$getRemainingItems(CraftingContainer container) {
		var list = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);

		for (var i = 0; i < list.size(); i++) {
			list.set(i, IngredientAction.getRemaining(container, i, kjs$getIngredientActions()));
		}

		return list;
	}

	default ItemStack kjs$assemble(CraftingContainer container, HolderLookup.Provider registryAccess) {
		var player = getPlayer(((CraftingContainerKJS) container).kjs$getMenu());

		if (!kjs$getStage().isEmpty()) {
			if (player == null || !player.kjs$getStages().has(kjs$getStage())) {
				return ItemStack.EMPTY;
			}
		}

		var modifyResult = kjs$getModifyResult();
		var result = getResultItem(registryAccess);
		//noinspection ConstantValue
		result = (result == null || result.isEmpty()) ? ItemStack.EMPTY : result.copy();

		if (!modifyResult.isEmpty()) {
			var event = new ModifyRecipeResultKubeEvent(player, new ContainerModifyRecipeCraftingGrid(container), result);
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
