package dev.latvian.kubejs.item.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.ItemStackJS;
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
	private final ItemStackJS item;

	public WeakNBTIngredientJS(ItemStackJS i) {
		item = i;
	}

	@Override
	public boolean test(ItemStackJS stack) {
		if (item.areItemsEqual(stack) && item.hasNBT() == stack.hasNBT()) {
			if (item.hasNBT()) {
				for (String key : item.getNbt().keySet()) {
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
				CompoundTag t = item.getMinecraftNbt();

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
			json.addProperty("type", "nbt_ingredient_predicate:nbt_includes");
			json.addProperty("nbt", item.getNbtString());
		}

		return json;
	}
}