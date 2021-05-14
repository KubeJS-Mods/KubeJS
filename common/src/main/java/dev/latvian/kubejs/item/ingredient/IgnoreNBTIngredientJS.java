package dev.latvian.kubejs.item.ingredient;

import dev.latvian.kubejs.item.ItemStackJS;
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
}