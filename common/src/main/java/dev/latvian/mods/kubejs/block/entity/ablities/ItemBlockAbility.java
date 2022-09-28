package dev.latvian.mods.kubejs.block.entity.ablities;

import dev.latvian.mods.kubejs.block.entity.ablities.wrappers.AbilityTypeWrapper;
import dev.latvian.mods.kubejs.block.entity.ablities.wrappers.ItemAbilityWrapper;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ItemBlockAbility extends BlockAbility<ItemStack> {

	public Map<String, AbilityTypeWrapper<ItemStack>> slots = new HashMap<>();

	public ItemBlockAbility(AbilityJS map) {
		super(map);
		for (var slot : map.slots()) {
			// todo, input / output / filter
			slots.put(slot.id(), new ItemAbilityWrapper(slot.limit().intValue()));
		}
	}

	@Override
	public Map<String, AbilityTypeWrapper<ItemStack>> getSlotMap() {
		return slots;
	}

	@Override
	public void markDirty(String slot) {

	}

	@Override
	public void onChanged(BiConsumer<String, AbilityTypeWrapper<ItemStack>> cb) {

	}

	@Override
	public void onSlotChanged(String slot, Consumer<AbilityTypeWrapper<ItemStack>> cb) {

	}
}
