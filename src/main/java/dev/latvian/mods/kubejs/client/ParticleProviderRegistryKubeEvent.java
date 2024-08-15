package dev.latvian.mods.kubejs.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

import java.util.function.Consumer;

public class ParticleProviderRegistryKubeEvent implements ClientKubeEvent {

	private final RegisterParticleProvidersEvent parent;

	public ParticleProviderRegistryKubeEvent(RegisterParticleProvidersEvent event) {
		parent = event;
	}

	public <T extends ParticleOptions> void register(ParticleType<T> type, SpriteSetParticleProvider<T> spriteProvider) {
		parent.registerSpriteSet(type, spriteProvider);
	}

	public <T extends ParticleOptions> void register(ParticleType<T> type, Consumer<KubeAnimatedParticle> particle) {
		parent.registerSpriteSet(type, set -> (type1, level, x, y, z, xSpeed, ySpeed, zSpeed) -> {
			var kube = new KubeAnimatedParticle(level, x, y, z, set);
			kube.setParticleSpeed(xSpeed, ySpeed, zSpeed);
			particle.accept(kube);
			return kube;
		});
	}

	public <T extends ParticleOptions> void register(ParticleType<T> type) {
		register(type, p -> {
		});
	}

	public <T extends ParticleOptions> void registerSpecial(ParticleType<T> type, ParticleProvider<T> provider) {
		parent.registerSpecial(type, provider);
	}

	@FunctionalInterface
	public interface SpriteSetParticleProvider<T extends ParticleOptions> extends ParticleEngine.SpriteParticleRegistration<T> {
		Particle create(T type, ClientLevel clientLevel, double x, double y, double z, SpriteSet sprites, double xSpeed, double ySpeed, double zSpeed);

		@Override
		default ParticleProvider<T> create(SpriteSet sprites) {
			return (type, level, x, y, z, xSpeed, ySpeed, zSpeed) -> create(type, level, x, y, z, sprites, xSpeed, ySpeed, zSpeed);
		}
	}
}
