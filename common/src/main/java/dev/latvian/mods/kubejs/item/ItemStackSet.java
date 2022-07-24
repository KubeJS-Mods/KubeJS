package dev.latvian.mods.kubejs.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ItemStackSet implements Iterable<ItemStack> {
	private static class Key {

		private final Item item;

		private final CompoundTag tag;
		private int hashCode = 0;

		private Key(ItemStack is) {
			item = is.getItem();
			tag = is.getTag();
		}

		@Override
		public int hashCode() {
			if (hashCode == 0) {
				hashCode = item == Items.AIR ? 0 : tag == null ? item.hashCode() : (item.hashCode() * 31 + tag.hashCode());

				if (hashCode == 0) {
					hashCode = 1;
				}
			}

			return hashCode;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Key k) {
				return item == k.item && hashCode() == k.hashCode() && Objects.equals(tag, k.tag);
			}

			return false;
		}

	}

	private final HashMap<Key, ItemStack> map = new HashMap<>();

	public void add(ItemStack stack) {
		if (!stack.isEmpty()) {
			map.putIfAbsent(new Key(stack), stack);
		}
	}

	public void addItem(Item item) {
		if (item != Items.AIR) {
			ItemStack stack = item.getDefaultInstance();
			map.putIfAbsent(new Key(stack), stack);
		}
	}

	public void remove(ItemStack stack) {
		if (!stack.isEmpty()) {
			map.remove(new Key(stack));
		}
	}

	public boolean contains(ItemStack stack) {
		return !stack.isEmpty() && map.containsKey(new Key(stack));
	}

	public List<ItemStack> toList() {
		return map.isEmpty() ? List.of() : new ArrayList<>(map.values());
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public int size() {
		return map.size();
	}

	@Override
	public Iterator<ItemStack> iterator() {
		return map.values().iterator();
	}

	@Override
	public void forEach(Consumer<? super ItemStack> action) {
		map.values().forEach(action);
	}

	public ItemStack getFirst() {
		return map.isEmpty() ? ItemStack.EMPTY : map.values().iterator().next();
	}
}
