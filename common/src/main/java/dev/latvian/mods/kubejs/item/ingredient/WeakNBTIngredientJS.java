package dev.latvian.mods.kubejs.item.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.ItemStackSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.Set;

/**
 * @author LatvianModder
 */
public final class WeakNBTIngredientJS implements IngredientJS {
	private final ItemStack item;

	public WeakNBTIngredientJS(ItemStack i) {
		item = i;
	}

	@Override
	public boolean test(ItemStack stack) {
		if (item.getItem() == stack.getItem() && item.hasTag() == stack.hasTag()) {
			if (item.hasTag()) {
				var t = item.getTag();

				for (var key : t.getAllKeys()) {
					if (!Objects.equals(t.get(key), stack.getTag().get(key))) {
						return false;
					}
				}
			}

			return true;
		}

		return false;
	}

	@Override
	public boolean testItem(Item i) {
		return item.getItem() == i;
	}

	@Override
	public void gatherStacks(ItemStackSet set) {
		set.add(item);
	}

	@Override
	public void gatherItemTypes(Set<Item> set) {
		set.add(item.getItem());
	}

	@Override
	public IngredientJS copy() {
		return new WeakNBTIngredientJS(item.copy());
	}

	@Override
	public JsonElement toJson() {
		var json = new JsonObject();
		json.addProperty("item", item.kjs$getId());

		if (item.hasTag()) {
			json.addProperty("type", "forge:partial_nbt");
			json.addProperty("nbt", item.kjs$getNbtString());
		}

		return json;
	}

	@Override
	public String toString() {
		var stack = item.toString().replaceAll("^'(.*)'$", "Item.of($1)");
		return stack + ".weakNBT()";

	}
}