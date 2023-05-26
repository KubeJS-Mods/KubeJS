package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.entity.EntityEventJS;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class FoodEatenEventJS extends EntityEventJS {
	private final Entity entity;
	private final ItemStack item;

	public FoodEatenEventJS(LivingEntity e, ItemStack is) {
		entity = e;
		item = is;
	}

	@Override
	public Entity getEntity() {
		return entity;
	}

	public ItemStack getItem() {
		return item;
	}
}