package dev.latvian.mods.kubejs.item.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * @author LatvianModder
 */
public final class WeakNBTIngredientJS implements IngredientJS {
	private final ItemStackJS item;

	public WeakNBTIngredientJS(ItemStackJS i) {
		item = i;
	}

	@Override
	public boolean test(ItemStackJS stack) {
		if (item.areItemsEqual(stack) && item.hasNBT() == stack.hasNBT()) {
			if (item.hasNBT()) {
				for (var key : item.getNbt().getAllKeys()) {
					if (!Objects.equals(item.getNbt().get(key), stack.getNbt().get(key))) {
						return false;
					}
				}
			}

			return true;
		}

		return false;
	}

	@Override
	public boolean testVanilla(ItemStack stack) {
		if (item.areItemsEqual(stack) && item.hasNBT() == stack.hasTag()) {
			if (item.hasNBT()) {
				var t = item.getNbt();

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
	public boolean testVanillaItem(Item i) {
		return item.getItem() == i;
	}

	@Override
	public Set<ItemStackJS> getStacks() {
		return item.getStacks();
	}

	@Override
	public Set<Item> getVanillaItems() {
		return Collections.singleton(item.getItem());
	}

	@Override
	public IngredientJS copy() {
		return new WeakNBTIngredientJS(item.copy());
	}

	@Override
	public JsonElement toJson() {
		var json = new JsonObject();
		json.addProperty("item", item.getId());

		if (item.hasNBT()) {
			json.addProperty("type", "forge:partial_nbt");
			json.addProperty("nbt", item.getNbtString());
		}

		return json;
	}

	@Override
	public String toString() {
		var stack = item.toString().replaceAll("^'(.*)'$", "Item.of($1)");
		return stack + ".weakNBT()";

	}
}