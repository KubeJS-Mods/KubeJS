package dev.latvian.mods.kubejs.entity;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public class EntityPotionEffectsJS {
	private final LivingEntity entity;

	public EntityPotionEffectsJS(LivingEntity e) {
		entity = e;
	}

	public void clear() {
		entity.removeAllEffects();
	}

	public Collection<MobEffectInstance> getActive() {
		return entity.getActiveEffects();
	}

	public Map<MobEffect, MobEffectInstance> getMap() {
		return entity.getActiveEffectsMap();
	}

	public boolean isActive(MobEffect mobEffect) {
		return mobEffect != null && entity.hasEffect(mobEffect);
	}

	public int getDuration(MobEffect mobEffect) {
		if (mobEffect != null) {
			var i = entity.getActiveEffectsMap().get(mobEffect);
			return i == null ? 0 : i.getDuration();
		}

		return 0;
	}

	@Nullable
	public MobEffectInstance getActive(MobEffect mobEffect) {
		return mobEffect == null ? null : entity.getEffect(mobEffect);
	}

	public void add(MobEffect mobEffect) {
		add(mobEffect, 200);
	}

	public void add(MobEffect mobEffect, int duration) {
		add(mobEffect, duration, 0);
	}

	public void add(MobEffect mobEffect, int duration, int amplifier) {
		add(mobEffect, duration, amplifier, false, true);
	}

	public void add(MobEffect mobEffect, int duration, int amplifier, boolean ambient, boolean showParticles) {
		if (mobEffect != null) {
			entity.addEffect(new MobEffectInstance(mobEffect, duration, amplifier, ambient, showParticles));
		}
	}

	public boolean isApplicable(MobEffectInstance effect) {
		return entity.canBeAffected(effect);
	}
}