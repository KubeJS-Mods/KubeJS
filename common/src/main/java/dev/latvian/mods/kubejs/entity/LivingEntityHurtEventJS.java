package dev.latvian.mods.kubejs.entity;

import dev.latvian.mods.kubejs.event.EventHandler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * @author LatvianModder
 */
public class LivingEntityHurtEventJS extends LivingEntityEventJS {
	public static final EventHandler EVENT = EventHandler.server(LivingEntityHurtEventJS.class).cancelable().legacy("entity.hurt");

	private final LivingEntity entity;
	private final DamageSource source;
	private final float amount;

	public LivingEntityHurtEventJS(LivingEntity entity, DamageSource source, float amount) {
		this.entity = entity;
		this.source = source;
		this.amount = amount;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(entity);
	}

	public DamageSource getSource() {
		return source;
	}

	public float getDamage() {
		return amount;
	}
}