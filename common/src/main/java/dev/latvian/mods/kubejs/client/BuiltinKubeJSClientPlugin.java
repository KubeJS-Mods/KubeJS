package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.bindings.event.ClientEvents;
import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import net.minecraft.client.Minecraft;

public class BuiltinKubeJSClientPlugin extends KubeJSPlugin {
	@Override
	public void clientInit() {
		Painter.INSTANCE.registerBuiltinObjects();
	}

	@Override
	public void registerEvents() {
		ClientEvents.GROUP.register();
	}

	@Override
	public void registerBindings(BindingsEvent event) {
		event.add("Client", Minecraft.getInstance());
		event.add("Painter", Painter.INSTANCE);
	}
}
