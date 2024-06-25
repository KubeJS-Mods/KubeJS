package dev.latvian.mods.kubejs.entity;

import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@Info("""
	Invoked after an entity is hurt by a damage source.
	""")
public class AfterLivingEntityHurtKubeEvent implements KubeLivingEntityEvent {
	private final LivingDamageEvent.Post event;

	public AfterLivingEntityHurtKubeEvent(LivingDamageEvent.Post event) {
		this.event = event;
	}

	@Override
	@Info("The entity that was hurt.")
	public LivingEntity getEntity() {
		return event.getEntity();
	}

	@Info("The damage source.")
	public DamageSource getSource() {
		return event.getSource();
	}

	@Info("The amount of damage.")
	public float getDamage() {
		return event.getNewDamage();
	}
}