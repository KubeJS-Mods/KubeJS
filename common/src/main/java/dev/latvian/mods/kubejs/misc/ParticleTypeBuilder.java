package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.BuilderBase;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;

public class ParticleTypeBuilder extends BuilderBase<ParticleType<?>> {
	public transient boolean overrideLimiter;
	public transient ParticleOptions.Deserializer deserializer;

	public ParticleTypeBuilder(ResourceLocation i) {
		super(i);
		overrideLimiter = false;
	}

	@Override
	public final RegistryObjectBuilderTypes<ParticleType<?>> getRegistryType() {
		return RegistryObjectBuilderTypes.PARTICLE_TYPE;
	}

	@Override
	public ParticleType<?> createObject() {
		if (deserializer != null) {
			return new ComplexParticleType(overrideLimiter, deserializer);
		}

		return new BasicParticleType(overrideLimiter);
	}

	public ParticleTypeBuilder overrideLimiter(boolean o) {
		overrideLimiter = o;
		return this;
	}

	// TODO: Figure out if this is even needed
	public ParticleTypeBuilder deserializer(ParticleOptions.Deserializer d) {
		deserializer = d;
		return this;
	}
}
