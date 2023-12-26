package dev.latvian.mods.kubejs.entity;

import net.minecraft.world.entity.LivingEntity;

public abstract class LivingEntityEventJS extends EntityEventJS {
	@Override
	public abstract LivingEntity getEntity();
}