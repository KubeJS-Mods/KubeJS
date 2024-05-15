package dev.latvian.mods.kubejs.entity;

import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.core.Holder;
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

	public Map<Holder<MobEffect>, MobEffectInstance> getMap() {
		return entity.getActiveEffectsMap();
	}

	public boolean isHolderActive(Holder<MobEffect> mobEffect) {
		return mobEffect != null && entity.hasEffect(mobEffect);
	}

	public boolean isActive(MobEffect mobEffect) {
		return isHolderActive(RegistryInfo.MOB_EFFECT.getHolderOf(mobEffect));
	}

	public int getDuration(MobEffect mobEffect) {
		return getHolderDuration(RegistryInfo.MOB_EFFECT.getHolderOf(mobEffect));
	}

	public int getHolderDuration(Holder<MobEffect> mobEffect) {
		var i = entity.getEffect(mobEffect);
		return i == null ? 0 : i.getDuration();
	}

	@Nullable
	public MobEffectInstance getActive(MobEffect mobEffect) {
		return getHolderActive(RegistryInfo.MOB_EFFECT.getHolderOf(mobEffect));
	}

	@Nullable
	public MobEffectInstance getHolderActive(Holder<MobEffect> mobEffect) {
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
			entity.addEffect(new MobEffectInstance(RegistryInfo.MOB_EFFECT.getHolderOf(mobEffect), duration, amplifier, ambient, showParticles));
		}
	}

	public boolean isApplicable(MobEffectInstance effect) {
		return entity.canBeAffected(effect);
	}
}