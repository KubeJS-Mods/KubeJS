package dev.latvian.mods.kubejs.misc;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public class ComplexParticleType extends ParticleType<ParticleOptions> {
	public ComplexParticleType(boolean bl, ParticleOptions.Deserializer<ParticleOptions> deserializer) {
		super(bl, deserializer);
	}

	@Override
	public Codec<ParticleOptions> codec() {
		return null;
	}
}
