package dev.latvian.mods.kubejs.item.ingredient;

import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ItemStackSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * @author LatvianModder
 */
public class MatchAllIngredientJS implements IngredientJS {
	public static MatchAllIngredientJS INSTANCE = new MatchAllIngredientJS();

	private MatchAllIngredientJS() {
	}

	@Override
	public boolean test(ItemStack stack) {
		return !stack.isEmpty();
	}

	@Override
	public boolean testItem(Item item) {
		return item != Items.AIR;
	}

	@Override
	public void gatherStacks(ItemStackSet set) {
		for (var stack : ItemStackJS.getList()) {
			set.add(stack.copy());
		}
	}

	@Override
	public ItemStack getFirst() {
		var list = ItemStackJS.getList();
		return list.isEmpty() ? ItemStack.EMPTY : list.get(0).copy();
	}

	@Override
	public IngredientJS not() {
		return ItemStackJS.EMPTY;
	}

	@Override
	public String toString() {
		return "'*'";
	}
}