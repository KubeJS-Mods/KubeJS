package dev.latvian.mods.kubejs.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class LivingEntityDeathEventJS extends LivingEntityEventJS {
	private final LivingEntity entity;
	private final DamageSource source;

	public LivingEntityDeathEventJS(LivingEntity entity, DamageSource source) {
		this.entity = entity;
		this.source = source;
	}

	@Override
	public LivingEntity getEntity() {
		return entity;
	}

	public DamageSource getSource() {
		return source;
	}
}