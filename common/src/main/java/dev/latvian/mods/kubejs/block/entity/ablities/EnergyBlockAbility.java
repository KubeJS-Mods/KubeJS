package dev.latvian.mods.kubejs.block.entity.ablities;

import dev.latvian.mods.kubejs.block.entity.ablities.wrappers.AbilityTypeWrapper;
import dev.latvian.mods.kubejs.block.entity.ablities.wrappers.IntegerAbilityWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EnergyBlockAbility extends BlockAbility<Integer> {
	public final Map<String, AbilityTypeWrapper<Integer>> slots = new HashMap<>();

	public EnergyBlockAbility(AbilityJS map) {
		super(map);
		for (var slot : map.slots()) {
			// todo, input / output / filter
			slots.put(slot.id(), new IntegerAbilityWrapper(slot.limit().intValue()));
		}
	}

	@Override
	public Map<String, AbilityTypeWrapper<Integer>> getSlotMap() {
		return slots;
	}

	@Override
	public void markDirty(String slot) {

	}

	@Override
	public void onChanged(BiConsumer<String, AbilityTypeWrapper<Integer>> cb) {

	}

	@Override
	public void onSlotChanged(String slot, Consumer<AbilityTypeWrapper<Integer>> cb) {

	}

}