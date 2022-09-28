package dev.latvian.mods.kubejs.block.entity.ablities.wrappers;

import net.minecraft.nbt.Tag;

public interface AbilityTypeWrapper<T> {
	boolean isEmpty();

	AbilityTypeWrapper<T> copy();

	default AbilityTypeWrapper<T> withCount(long num) {
		AbilityTypeWrapper<T> copy = copy();
		copy.setCount(num);
		return copy;
	}

	default AbilityTypeWrapper<T> withRaw(T raw) {
		AbilityTypeWrapper<T> copy = copy();
		copy.setRaw(raw);
		return copy;
	}

	void setCount(long num);

	long getCount();

	long getMax();

	// @returns remainder
	default long safeSetCount(long num, boolean simulate) {
		var max = getMax();
		var initCount = getCount();
		if (num > max) {
			if (!simulate) {
				setCount(max);
			}
			return max - num;
		}
		if (num < 0) {
			if (!simulate) {
				setCount(0);
			}
			return Math.abs(num);
		}
		if (!simulate) {
			setCount(num);
		}
		return initCount - num;
	}

	default long safeSetCount(long num) {
		return safeSetCount(num, false);
	}

	// @returns remainder
	default long shrink(long num, boolean simulate) {
		return safeSetCount(getCount() - num, simulate);
	}

	default long shrink(long num) {
		return shrink(num, false);
	}

	// @returns remainder
	default long grow(long num, boolean simulate) {
		return safeSetCount(getCount() + num, simulate);
	}

	default long grow(long num) {
		return grow(num, false);
	}

	T getRaw();

	void setRaw(T other);

	// i.e, item.id == other.id
	boolean compatible(T other);

	// These do mutate *this* instance, yes this is weird behavior
	Tag toTag();

	AbilityTypeWrapper<T> fromTag(Tag tag);
}
