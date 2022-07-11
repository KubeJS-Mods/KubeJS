package dev.latvian.mods.kubejs.entity;

import dev.latvian.mods.kubejs.event.EventHandler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * @author LatvianModder
 */
public class LivingEntityDeathEventJS extends LivingEntityEventJS {
	public static final EventHandler EVENT = EventHandler.server(LivingEntityDeathEventJS.class).name("entityDeath").cancelable().legacy("entity.death");

	private final LivingEntity entity;
	private final DamageSource source;

	public LivingEntityDeathEventJS(LivingEntity entity, DamageSource source) {
		this.entity = entity;
		this.source = source;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(entity);
	}

	public DamageSource getSource() {
		return source;
	}
}