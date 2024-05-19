package dev.latvian.mods.kubejs.client;

import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.Util;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Consumer;

public class ParticleProviderRegistryEventJS extends EventJS {

	public <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider<T> provider) {
		ParticleProviderRegistry.register(type, provider);
	}

	public <T extends ParticleOptions> void registerSimple(ParticleType<T> type, Consumer<KubeAnimatedParticle> particle) {
		ParticleProviderRegistry.register(type, set -> (t, level, x, y, z, dx, dy, dz) -> Util.make(new KubeAnimatedParticle(level, set, x, y, z), particle));
	}

	public <T extends ParticleOptions> void registerSimple(ParticleType<T> type) {
		registerSimple(type, particle -> {});
	}

	@FunctionalInterface
	public interface ParticleProvider<T extends ParticleOptions> extends ParticleProviderRegistry.DeferredParticleProvider<T> {
		Particle create(T particleOptions, ClientLevel level, ParticleProviderRegistry.ExtendedSpriteSet spriteSet, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed);

		@Override
		default net.minecraft.client.particle.ParticleProvider<T> create(ParticleProviderRegistry.ExtendedSpriteSet spriteSet) {
			return (t, level, x, y, z, xSpeed, ySpeed, zSpeed) -> create(t, level, spriteSet, x, y, z, xSpeed, ySpeed, zSpeed);
		}
	}
}
