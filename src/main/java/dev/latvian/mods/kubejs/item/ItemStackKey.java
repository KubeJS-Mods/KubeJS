package dev.latvian.mods.kubejs.item;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ItemStackKey {
	public static ItemStackKey EMPTY = new ItemStackKey(Items.AIR, null);

	public static ItemStackKey of(ItemStack stack) {
		if (stack.isEmpty()) {
			return EMPTY;
		} else if (stack.getComponents().isEmpty()) {
			return stack.getItem().kjs$getTypeItemStackKey();
		}

		return new ItemStackKey(stack.getItem(), stack.getComponentsPatch());
	}

	public final Item item;
	public final DataComponentPatch patch;
	private int hashCode = 0;

	public ItemStackKey(Item item, DataComponentPatch patch) {
		this.item = item;
		this.patch = patch;
	}

	@Override
	public int hashCode() {
		if (hashCode == 0) {
			hashCode = item == Items.AIR ? 0 : (item.hashCode() * 31 + patch.hashCode());

			if (hashCode == 0) {
				hashCode = 1;
			}
		}

		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ItemStackKey k) {
			return item == k.item && hashCode() == k.hashCode() && patch.equals(k.patch);
		}

		return false;
	}

}