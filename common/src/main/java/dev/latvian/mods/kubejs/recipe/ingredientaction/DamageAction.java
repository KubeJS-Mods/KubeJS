package dev.latvian.mods.kubejs.recipe.ingredientaction;

import com.google.gson.JsonObject;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

public class DamageAction extends IngredientAction {
	public final int amount;

	public DamageAction(int a) {
		amount = a;
	}

	@Override
	public ItemStack transform(ItemStack old, int index, CraftingContainer container) {
		old.setDamageValue(old.getDamageValue() + amount);
		return old.getDamageValue() >= old.getMaxDamage() ? ItemStack.EMPTY : old;
	}

	@Override
	public String getType() {
		return "damage";
	}

	@Override
	public void toJson(JsonObject json) {
		json.addProperty("damage", amount);
	}
}
