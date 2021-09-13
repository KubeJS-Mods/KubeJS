package dev.latvian.kubejs.item.ingredient;

import dev.latvian.kubejs.item.ItemStackJS;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author LatvianModder
 */
public class MatchAllIngredientJS implements IngredientJS {
	public static MatchAllIngredientJS INSTANCE = new MatchAllIngredientJS();

	private MatchAllIngredientJS() {
	}

	@Override
	public boolean test(ItemStackJS stack) {
		return !stack.isEmpty();
	}

	@Override
	public boolean testVanilla(ItemStack stack) {
		return !stack.isEmpty();
	}

	@Override
	public boolean testVanillaItem(Item item) {
		return item != Items.AIR;
	}

	@Override
	public Set<ItemStackJS> getStacks() {
		Set<ItemStackJS> set = new LinkedHashSet<>();

		for (ItemStackJS stack : ItemStackJS.getList()) {
			set.add(stack.copy());
		}

		return set;
	}

	@Override
	public ItemStackJS getFirst() {
		List<ItemStackJS> list = ItemStackJS.getList();
		return list.isEmpty() ? ItemStackJS.EMPTY : list.get(0).copy();
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