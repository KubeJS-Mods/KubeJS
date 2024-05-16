package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;

public class REIIntegration extends KubeJSPlugin {
	@Override
	public void registerEvents(EventGroupRegistry registry) {
		registry.register(REIEvents.GROUP);
	}
}