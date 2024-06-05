package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.bindings.event.ClientEvents;
import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.util.ScheduledEvents;
import net.minecraft.client.Minecraft;

public class BuiltinKubeJSClientPlugin implements KubeJSPlugin {
	@Override
	public void clientInit() {
		Painter.INSTANCE.registerBuiltinObjects();
	}

	@Override
	public void registerEvents(EventGroupRegistry registry) {
		registry.register(ClientEvents.GROUP);
	}

	@Override
	public void registerBindings(BindingRegistry bindings) {
		bindings.add("Client", Minecraft.getInstance());
		bindings.add("Painter", Painter.INSTANCE);

		if (bindings.type().isClient()) {
			var se = Minecraft.getInstance().kjs$getScheduledEvents();

			bindings.add("setTimeout", new ScheduledEvents.TimeoutJSFunction(se, false, false));
			bindings.add("clearTimeout", new ScheduledEvents.TimeoutJSFunction(se, true, false));
			bindings.add("setInterval", new ScheduledEvents.TimeoutJSFunction(se, false, true));
			bindings.add("clearInterval", new ScheduledEvents.TimeoutJSFunction(se, true, true));
		}
	}
}
