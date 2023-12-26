package dev.latvian.mods.kubejs.recipe.ingredientaction;

import com.google.gson.JsonObject;
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
		var callback = MAP.get(id);
		return callback == null ? ItemStack.EMPTY : callback.transform(old, index, container).copy();
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
