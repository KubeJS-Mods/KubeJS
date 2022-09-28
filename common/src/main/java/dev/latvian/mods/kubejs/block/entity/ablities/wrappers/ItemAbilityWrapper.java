package dev.latvian.mods.kubejs.block.entity.ablities.wrappers;

import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public class ItemAbilityWrapper implements AbilityTypeWrapper<ItemStack> {
	static AbilityTypeWrapper<ItemStackJS> a = null;
	public ItemStack item = ItemStack.EMPTY;
	public int max;


	public ItemAbilityWrapper(int max) {
		this.max = max;
	}

	@Override
	public boolean isEmpty() {
		return item.isEmpty();
	}

	@Override
	public AbilityTypeWrapper<ItemStack> copy() {
		var copy = new ItemAbilityWrapper(max);
		copy.setRaw(item.copy());
		return copy;
	}

	@Override
	public void setCount(long num) {
		item.setCount(UtilsJS.parseInt(num, 0));
	}

	@Override
	public long getCount() {
		return item.getCount();
	}

	@Override
	public long getMax() {
		return max;
	}

	@Override
	public ItemStack getRaw() {
		return item;
	}

	@Override
	public void setRaw(ItemStack other) {
		item = other;
	}

	@Override
	public boolean compatible(ItemStack other) {
		return item.isEmpty() || item.kjs$equalsIgnoringCount(other);
	}

	@Override
	public Tag toTag() {
		CompoundTag nbt = new CompoundTag();
		nbt.put("item", StringTag.valueOf(item.kjs$getId()));
		nbt.put("count", LongTag.valueOf(item.getCount()));
		if (item.hasTag()) {
			nbt.put("nbt", item.getTag());
		}
		return nbt;
	}

	@Override
	public AbilityTypeWrapper<ItemStack> fromTag(Tag tag) {
		item = ItemStackJS.of(tag);
		return this;
	}
}
