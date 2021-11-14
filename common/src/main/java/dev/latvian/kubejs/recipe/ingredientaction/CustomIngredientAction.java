package dev.latvian.kubejs.recipe.ingredientaction;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.InventoryJS;
import dev.latvian.kubejs.item.ItemStackJS;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CustomIngredientAction extends IngredientAction {
	public static final Map<String, CustomIngredientActionCallback> MAP = new HashMap<>();

	public final String id;

	public CustomIngredientAction(String i) {
		id = i;
	}

	@Override
	public ItemStack transform(ItemStack old, int index, CraftingContainer container) {
		CustomIngredientActionCallback callback = MAP.get(id);
		return callback == null ? ItemStack.EMPTY : ItemStackJS.of(callback.transform(ItemStackJS.of(old), index, new InventoryJS(container))).getItemStack().copy();
	}

	@Override
	public String getType() {
		return "custom";
	}

	@Override
	public void toJson(JsonObject json) {
		json.addProperty("id", id);
	}
}
