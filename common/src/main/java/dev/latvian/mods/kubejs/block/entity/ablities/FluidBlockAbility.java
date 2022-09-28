package dev.latvian.mods.kubejs.block.entity.ablities;

import dev.latvian.mods.kubejs.block.entity.ablities.wrappers.AbilityTypeWrapper;
import dev.latvian.mods.kubejs.block.entity.ablities.wrappers.FluidAbilityWrapper;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FluidBlockAbility extends BlockAbility<FluidStackJS> {
	public Map<String, AbilityTypeWrapper<FluidStackJS>> slots = new HashMap<>();

	public FluidBlockAbility(AbilityJS map) {
		super(map);
		for (var slot : map.slots()) {
			// todo, input / output / filter
			slots.put(slot.id(), new FluidAbilityWrapper(slot.limit().longValue()));
		}
	}

	@Override
	public Map<String, AbilityTypeWrapper<FluidStackJS>> getSlotMap() {
		return slots;
	}

	@Override
	public void markDirty(String slot) {

	}

	@Override
	public void onChanged(BiConsumer<String, AbilityTypeWrapper<FluidStackJS>> cb) {

	}

	@Override
	public void onSlotChanged(String slot, Consumer<AbilityTypeWrapper<FluidStackJS>> cb) {

	}
}
