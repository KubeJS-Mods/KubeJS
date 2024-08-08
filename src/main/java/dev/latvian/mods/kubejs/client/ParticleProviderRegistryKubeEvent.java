package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.event.KubeEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

public class ParticleProviderRegistryKubeEvent implements KubeEvent {

	private final RegisterParticleProvidersEvent parent;

	public ParticleProviderRegistryKubeEvent(RegisterParticleProvidersEvent event) {
		parent = event;
	}

	// TODO: Oh dear
}
