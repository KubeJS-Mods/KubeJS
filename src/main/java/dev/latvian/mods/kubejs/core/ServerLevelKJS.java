package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;

@RemapPrefixForJS("kjs$")
public interface ServerLevelKJS extends LevelKJS, WithPersistentData {
	@Override
	default ServerLevel kjs$self() {
		return (ServerLevel) this;
	}

	@Override
	default void kjs$spawnParticles(ParticleOptions options, boolean overrideLimiter, double x, double y, double z, double vx, double vy, double vz, int count, double speed) {
		for (var player : kjs$self().players()) {
			kjs$self().sendParticles(player, options, overrideLimiter, x, y, z, count, vx, vy, vz, speed);
		}
	}
}
