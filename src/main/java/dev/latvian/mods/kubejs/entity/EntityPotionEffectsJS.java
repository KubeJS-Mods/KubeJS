package dev.latvian.mods.kubejs.entity;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.CommonHooks;
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

	public boolean isActive(Holder<MobEffect> mobEffect) {
		return mobEffect != null && entity.hasEffect(mobEffect);
	}

	public int getDuration(Holder<MobEffect> mobEffect) {
		var i = entity.getEffect(mobEffect);
		return i == null ? 0 : i.getDuration();
	}

	@Nullable
	public MobEffectInstance getActive(Holder<MobEffect> mobEffect) {
		return mobEffect == null ? null : entity.getEffect(mobEffect);
	}

	public void add(Holder<MobEffect> mobEffect) {
		add(mobEffect, 200);
	}

	public void add(Holder<MobEffect> mobEffect, int duration) {
		add(mobEffect, duration, 0);
	}

	public void add(Holder<MobEffect> mobEffect, int duration, int amplifier) {
		add(mobEffect, duration, amplifier, false, true);
	}

	public void add(Holder<MobEffect> mobEffect, int duration, int amplifier, boolean ambient, boolean showParticles) {
		entity.addEffect(new MobEffectInstance(mobEffect, duration, amplifier, ambient, showParticles));
	}

	public boolean isApplicable(MobEffectInstance effect) {
		// TODO: source?
		return CommonHooks.canMobEffectBeApplied(entity, effect, null);
	}
}