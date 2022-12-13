package dev.latvian.mods.kubejs.recipe.ingredientaction;

import com.google.gson.JsonObject;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

public class ReplaceAction extends IngredientAction {
	public final ItemStack item;

	public ReplaceAction(ItemStack a) {
		item = a;
	}

	@Override
	public ItemStack transform(ItemStack old, int index, CraftingContainer container) {
		return item.copy();
	}

	@Override
	public String getType() {
		return "replace";
	}

	@Override
	public void toJson(JsonObject json) {
		json.add("item", item.toJsonJS());
	}
}
