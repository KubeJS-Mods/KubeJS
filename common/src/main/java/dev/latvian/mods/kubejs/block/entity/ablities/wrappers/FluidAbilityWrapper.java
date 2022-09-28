package dev.latvian.mods.kubejs.block.entity.ablities.wrappers;

import dev.latvian.mods.kubejs.fluid.EmptyFluidStackJS;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;

public class FluidAbilityWrapper implements AbilityTypeWrapper<FluidStackJS> {
	public FluidStackJS fluid = EmptyFluidStackJS.INSTANCE;
	public long max;

	public FluidAbilityWrapper(long max) {
		this.max = max;
	}

	@Override
	public boolean isEmpty() {
		return fluid.isEmpty();
	}

	@Override
	public AbilityTypeWrapper<FluidStackJS> copy() {
		var copy = new FluidAbilityWrapper(max);
		copy.setRaw(getRaw().copy());
		return copy;
	}

	@Override
	public void setCount(long num) {
		fluid.setAmount(num);
	}

	@Override
	public long getCount() {
		return fluid.getAmount();
	}

	@Override
	public long getMax() {
		return max;
	}

	@Override
	public FluidStackJS getRaw() {
		return fluid;
	}

	@Override
	public void setRaw(FluidStackJS other) {
		fluid = other;
	}

	@Override
	public boolean compatible(FluidStackJS other) {
		return other != null && fluid.getId().equals(other.getId());
	}

	@Override
	public Tag toTag() {
		ListTag tag = new ListTag();
		tag.add(fluid.toNBT());
		tag.add(LongTag.valueOf(max));
		return tag;
	}

	@Override
	public AbilityTypeWrapper<FluidStackJS> fromTag(Tag tag) {
		ListTag nbt = UtilsJS.cast(tag);
		fluid = FluidStackJS.of(nbt.get(0));
		max = ((LongTag) nbt.get(1)).getAsLong();
		return this;
	}
}
