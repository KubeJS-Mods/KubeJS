package dev.latvian.mods.kubejs.integration.jei;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;

public class JEIIntegration implements KubeJSPlugin {
	@Override
	public void registerEvents(EventGroupRegistry registry) {
		registry.register(JEIEvents.GROUP);
	}
}