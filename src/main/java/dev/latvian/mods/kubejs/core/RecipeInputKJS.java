package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.util.SlotFilter;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.ArrayList;
import java.util.List;

@RemapPrefixForJS("kjs$")
public interface RecipeInputKJS {
	default RecipeInput kjs$self() {
		return (RecipeInput) this;
	}

	default List<ItemStack> kjs$findAll(SlotFilter filter) {
		var list = new ArrayList<ItemStack>();
		int size = kjs$self().size();

		for (int i = 0; i < size; i++) {
			var stack = kjs$self().getItem(i);

			if (filter.checkFilter(i, stack)) {
				list.add(stack.copy());
			}
		}

		return list;
	}

	default List<ItemStack> kjs$findAll() {
		return kjs$findAll(SlotFilter.EMPTY);
	}

	default ItemStack find(SlotFilter filter, int skip) {
		for (var item : kjs$findAll(filter)) {
			if (skip == 0) {
				return item;
			}

			skip--;
		}

		return ItemStack.EMPTY;
	}

	default ItemStack find(SlotFilter filter) {
		return find(filter, 0);
	}
}
