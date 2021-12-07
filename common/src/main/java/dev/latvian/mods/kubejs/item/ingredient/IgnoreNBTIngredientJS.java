package dev.latvian.mods.kubejs.item.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.Set;

/**
 * @author LatvianModder
 */
public final class IgnoreNBTIngredientJS implements IngredientJS {
	private final ItemStackJS item;

	public IgnoreNBTIngredientJS(ItemStackJS i) {
		item = i;
	}

	@Override
	public boolean test(ItemStackJS stack) {
		return item.areItemsEqual(stack);
	}

	@Override
	public boolean testVanilla(ItemStack stack) {
		return item.getItem() == stack.getItem();
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
		return new IgnoreNBTIngredientJS(item.copy());
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("item", item.getId());

		if (Platform.isForge()) {
			json.addProperty("type", "kubejs:ignore_nbt");
		}

		return json;
	}

	@Override
	public String toString() {
		String stack = item.toString().replaceAll("^'(.*)'$", "Item.of($1)");
		return stack + ".ignoreNBT()";
	}
}