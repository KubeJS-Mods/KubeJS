package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.bindings.event.ClientEvents;
import dev.latvian.mods.kubejs.client.ParticleProviderRegistryEventJS;
import net.minecraft.client.particle.ParticleEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin {

	@Inject(method = "registerProviders", at = @At("RETURN"))
	private void registerParticleProvidersKJS(CallbackInfo ci) {
		ClientEvents.PARTICLE_PROVIDER_REGISTRY.post(new ParticleProviderRegistryEventJS());
	}
}
