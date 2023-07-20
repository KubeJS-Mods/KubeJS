package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.entity.EntityEventJS;
import dev.latvian.mods.kubejs.typings.JsInfo;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@JsInfo("""
		Invoked when an entity eats food.
		""")
public class FoodEatenEventJS extends EntityEventJS {
	private final Entity entity;
	private final ItemStack item;

	public FoodEatenEventJS(LivingEntity e, ItemStack is) {
		entity = e;
		item = is;
	}

	@Override
	@JsInfo("The entity that ate the food.")
	public Entity getEntity() {
		return entity;
	}

	@JsInfo("The food that was eaten.")
	public ItemStack getItem() {
		return item;
	}
}