package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.client.ParticleGenerator;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class ParticleTypeBuilder extends BuilderBase<ParticleType<?>> {
	public transient boolean overrideLimiter;
	public transient ParticleOptions.Deserializer deserializer;
	public transient Consumer<ParticleGenerator> assetGen;

	public ParticleTypeBuilder(ResourceLocation i) {
		super(i);
		overrideLimiter = false;
		assetGen = g -> g.texture(id);
	}

	@Override
	public final RegistryInfo getRegistryType() {
		return RegistryInfo.PARTICLE_TYPE;
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

	public ParticleTypeBuilder textures(Consumer<ParticleGenerator> gen) {
		assetGen = gen;
		return this;
	}

	// TODO: Figure out if this is even needed
	public ParticleTypeBuilder deserializer(ParticleOptions.Deserializer d) {
		deserializer = d;
		return this;
	}

	@Override
	public void generateAssetJsons(AssetJsonGenerator generator) {
		generator.particle(id, assetGen);
	}
}
