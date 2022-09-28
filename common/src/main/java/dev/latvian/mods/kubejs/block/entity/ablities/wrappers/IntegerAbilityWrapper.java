package dev.latvian.mods.kubejs.block.entity.ablities.wrappers;

import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.Tag;

public class IntegerAbilityWrapper implements AbilityTypeWrapper<Integer> {
	public int amount = 0;
	public int max = 1;

	public IntegerAbilityWrapper(int max) {
		this.max = max;
	}

	@Override
	public boolean isEmpty() {
		return amount == 0;
	}

	@Override
	public AbilityTypeWrapper<Integer> copy() {
		var copy = new IntegerAbilityWrapper(max);
		copy.setRaw(getRaw());
		return copy;
	}

	@Override
	public void setCount(long num) {
		amount = UtilsJS.parseInt(num, 0);
	}

	@Override
	public long getCount() {
		return amount;
	}

	@Override
	public long getMax() {
		return max;
	}

	@Override
	public Integer getRaw() {
		return amount;
	}

	@Override
	public void setRaw(Integer other) {
		amount = other;
	}

	@Override
	public boolean compatible(Integer other) {
		return true;
	}

	@Override
	public Tag toTag() {
		return new IntArrayTag(new int[]{amount, max});
	}

	@Override
	public AbilityTypeWrapper<Integer> fromTag(Tag tag) {
		IntArrayTag nbt = UtilsJS.cast(tag);
		amount = nbt.get(0).getAsInt();
		max = nbt.get(1).getAsInt();
		return this;
	}
}
