package dev.latvian.mods.kubejs.level;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Optional;

public record ExplosionProperties(
	@Nullable Entity source,
	@Nullable DamageSource damageSource,
	@Nullable ExplosionDamageCalculator damageCalculator,
	Optional<Float> strength,
	Optional<Boolean> causesFire,
	@Nullable Level.ExplosionInteraction mode,
	Optional<Boolean> particles,
	@Nullable ParticleOptions smallParticles,
	@Nullable ParticleOptions largeParticles,
	@Nullable Holder<SoundEvent> explosionSound
) {
	public Explosion explode(Level level, double x, double y, double z) {
		return level.explode(
			source,
			damageSource,
			damageCalculator,
			x, y, z,
			strength.orElse(3F),
			causesFire.orElse(Boolean.FALSE),
			mode == null ? Level.ExplosionInteraction.NONE : mode,
			particles.orElse(Boolean.TRUE),
			smallParticles == null ? ParticleTypes.EXPLOSION : smallParticles,
			largeParticles == null ? ParticleTypes.EXPLOSION_EMITTER : largeParticles,
			explosionSound == null ? SoundEvents.GENERIC_EXPLODE : explosionSound
		);
	}
}