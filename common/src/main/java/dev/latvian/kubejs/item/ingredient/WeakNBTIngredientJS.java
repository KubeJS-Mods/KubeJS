package dev.latvian.kubejs.item.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.util.ConsoleJS;
import me.shedaniel.architectury.platform.Platform;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * @author LatvianModder
 */
public final class WeakNBTIngredientJS implements IngredientJS {
	private static final boolean NBTIP_INSTALLED = Platform.isModLoaded("nbt_ingredient_predicate");

	private final ItemStackJS item;

	public WeakNBTIngredientJS(ItemStackJS i) {
		item = i;
	}

	@Override
	public boolean test(ItemStackJS stack) {
		if (item.areItemsEqual(stack) && item.hasNBT() == stack.hasNBT()) {
			if (item.hasNBT()) {
				for (String key : item.getNbt().getAllKeys()) {
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
				CompoundTag t = item.getNbt();

				for (String key : t.getAllKeys()) {
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
		JsonObject json = new JsonObject();
		json.addProperty("item", item.getId());

		if (item.hasNBT()) {
			if (NBTIP_INSTALLED) {
				json.addProperty("type", "nbt_ingredient_predicate:nbt_includes");
			} else {
				json.addProperty("type", "forge:nbt");
				ConsoleJS.SERVER.setLineNumber(true);
				ConsoleJS.SERVER.error("weakNBT() requires 'NBT Ingredient Predicate' mod to be installed! Defaulting to exact match");
				ConsoleJS.SERVER.setLineNumber(false);
			}

			json.addProperty("nbt", item.getNbtString());
		}

		return json;
	}

	@Override
	public String toString() {
		String stack = item.toString().replaceAll("^'(.*)'$", "Item.of($1)");
		return stack + ".weakNBT()";

	}
}