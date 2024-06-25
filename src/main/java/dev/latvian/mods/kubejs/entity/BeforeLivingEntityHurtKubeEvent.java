package dev.latvian.mods.kubejs.entity;

import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@Info("""
	Invoked before an entity is hurt by a damage source.
	""")
public class BeforeLivingEntityHurtKubeEvent implements KubeLivingEntityEvent {
	private final LivingDamageEvent.Pre event;

	public BeforeLivingEntityHurtKubeEvent(LivingDamageEvent.Pre event) {
		this.event = event;
	}

	@Override
	@Info("The entity that was hurt.")
	public LivingEntity getEntity() {
		return event.getEntity();
	}

	@Info("The damage source.")
	public DamageSource getSource() {
		return event.getContainer().getSource();
	}

	@Info("The amount of damage.")
	public float getDamage() {
		return event.getContainer().getNewDamage();
	}

	public void setDamage(float damage) {
		event.getContainer().setNewDamage(damage);
	}
}