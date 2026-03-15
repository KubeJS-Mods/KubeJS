package dev.latvian.mods.kubejs.level;

import dev.latvian.mods.kubejs.util.Cast;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
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
	public void explode(Level level, double x, double y, double z) {
		var blockParticles = (particles.orElse(Boolean.TRUE))
			? WeightedList.builder()
			.add(new ExplosionParticleInfo(ParticleTypes.POOF, 0.5F, 1.0F))
			.add(new ExplosionParticleInfo(ParticleTypes.SMOKE, 1.0F, 1.0F))
			.build()
			: WeightedList.<ExplosionParticleInfo>builder().build();

		level.explode(
			source,
			damageSource,
			damageCalculator,
			x, y, z,
			strength.orElse(3F),
			causesFire.orElse(Boolean.FALSE),
			mode == null ? Level.ExplosionInteraction.NONE : mode,
			smallParticles == null ? ParticleTypes.EXPLOSION : smallParticles,
			largeParticles == null ? ParticleTypes.EXPLOSION_EMITTER : largeParticles,
			Cast.to(blockParticles),
			explosionSound == null ? SoundEvents.GENERIC_EXPLODE : explosionSound
		);
	}
}