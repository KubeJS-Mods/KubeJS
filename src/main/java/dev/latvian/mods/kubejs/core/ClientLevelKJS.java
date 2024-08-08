package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.client.KubeAnimatedParticle;
import dev.latvian.mods.kubejs.player.EntityArrayList;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;

@RemapPrefixForJS("kjs$")
public interface ClientLevelKJS extends LevelKJS {
	@Override
	default ClientLevel kjs$self() {
		return (ClientLevel) this;
	}

	@Override
	default ScriptType kjs$getScriptType() {
		return ScriptType.CLIENT;
	}

	@Override
	default EntityArrayList kjs$getEntities() {
		return new EntityArrayList(kjs$self(), kjs$self().entitiesForRendering());
	}

	@Override
	default void kjs$spawnParticles(ParticleOptions options, boolean overrideLimiter, double x, double y, double z, double vx, double vy, double vz, int count, double speed) {
		if (count == 0) {
			double d0 = speed * vx;
			double d2 = speed * vy;
			double d4 = speed * vz;

			try {
				kjs$self().addParticle(options, overrideLimiter, x, y, z, d0, d2, d4);
			} catch (Throwable var17) {
			}
		} else {
			var random = kjs$self().random;

			for (int i = 0; i < count; ++i) {
				double ox = random.nextGaussian() * vx;
				double oy = random.nextGaussian() * vy;
				double oz = random.nextGaussian() * vz;
				double d6 = random.nextGaussian() * speed;
				double d7 = random.nextGaussian() * speed;
				double d8 = random.nextGaussian() * speed;

				try {
					kjs$self().addParticle(options, overrideLimiter, x + ox, y + oy, z + oz, d6, d7, d8);
				} catch (Throwable var16) {
					return;
				}
			}
		}
	}

	default KubeAnimatedParticle kubeParticle(double x, double y, double z, SpriteSet spriteSet) {
		return new KubeAnimatedParticle(kjs$self(), x, y, z, spriteSet);
	}
}
