package dev.latvian.mods.kubejs.entity;

import net.minecraft.world.entity.LivingEntity;

public interface KubeLivingEntityEvent extends KubeEntityEvent {
	@Override
	LivingEntity getEntity();
}