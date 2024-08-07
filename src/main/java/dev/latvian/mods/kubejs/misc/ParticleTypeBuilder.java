package dev.latvian.mods.kubejs.misc;

import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

@ReturnsSelf
public class ParticleTypeBuilder extends BuilderBase<ParticleType<?>> {
	public transient boolean overrideLimiter;
	public transient MapCodec<ParticleOptions> codec;
	public transient StreamCodec<? super RegistryFriendlyByteBuf, ParticleOptions> streamCodec;

	public ParticleTypeBuilder(ResourceLocation i) {
		super(i);
		overrideLimiter = false;
	}

	@Override
	public ParticleType<?> createObject() {
		if (codec != null && streamCodec != null) {
			return new ComplexParticleType(this);
		}

		return new BasicParticleType(overrideLimiter);
	}

	public ParticleTypeBuilder overrideLimiter(boolean o) {
		overrideLimiter = o;
		return this;
	}

	public ParticleTypeBuilder codec(MapCodec<ParticleOptions> c) {
		codec = c;
		return this;
	}

	public ParticleTypeBuilder streamCodec(StreamCodec<? super RegistryFriendlyByteBuf, ParticleOptions> s) {
		streamCodec = s;
		return this;
	}
}
