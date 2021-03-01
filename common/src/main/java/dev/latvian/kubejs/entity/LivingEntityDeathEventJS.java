package dev.latvian.kubejs.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * @author LatvianModder
 */
public class LivingEntityDeathEventJS extends LivingEntityEventJS {
	private final LivingEntity entity;
	private final DamageSource source;

	public LivingEntityDeathEventJS(LivingEntity entity, DamageSource source) {
		this.entity = entity;
		this.source = source;
	}

	@Override
	public boolean canCancel() {
		return true;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(entity);
	}

	public DamageSourceJS getSource() {
		return new DamageSourceJS(getWorld(), source);
	}
}