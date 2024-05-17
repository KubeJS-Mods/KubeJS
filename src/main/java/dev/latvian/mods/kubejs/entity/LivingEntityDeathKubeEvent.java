package dev.latvian.mods.kubejs.entity;

import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

@Info("""
	Invoked before a living entity dies.
			
	**NOTE**: You need to set hp to > 0 besides cancelling the event to prevent the entity from dying.
	""")
public class LivingEntityDeathKubeEvent implements KubeLivingEntityEvent {
	private final LivingEntity entity;
	private final DamageSource source;

	public LivingEntityDeathKubeEvent(LivingEntity entity, DamageSource source) {
		this.entity = entity;
		this.source = source;
	}

	@Override
	@Info("The entity that dies.")
	public LivingEntity getEntity() {
		return entity;
	}

	@Info("The damage source that triggers the death.")
	public DamageSource getSource() {
		return source;
	}
}