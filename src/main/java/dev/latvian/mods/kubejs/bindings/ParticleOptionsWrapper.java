package dev.latvian.mods.kubejs.bindings;

import com.google.gson.JsonElement;
import com.mojang.brigadier.StringReader;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import org.joml.Vector3f;

public interface ParticleOptionsWrapper {
	DustParticleOptions ERROR = new DustParticleOptions(new Vector3f(0F, 0F, 0F), 1F);

	static ParticleOptions wrap(RegistryAccessContainer registries, Object o) {
		if (o instanceof ParticleOptions po) {
			return po;
		} else if (o != null) {
			try {
				var reader = new StringReader(o instanceof JsonElement j ? j.getAsString() : o.toString());
				return ParticleArgument.readParticle(reader, registries.access());
			} catch (Exception ex) {
				throw new RuntimeException("Failed to parse ParticleOptions from " + o, ex);
			}
		}

		return ERROR;
	}

	static ParticleOptions create(ParticleOptions options) {
		return options;
	}
}
