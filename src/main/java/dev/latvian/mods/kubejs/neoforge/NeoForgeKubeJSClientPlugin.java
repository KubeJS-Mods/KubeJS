package dev.latvian.mods.kubejs.neoforge;

import dev.latvian.mods.kubejs.client.BuiltinKubeJSClientPlugin;
import dev.latvian.mods.kubejs.client.KubeJSClientEventHandler;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

public class NeoForgeKubeJSClientPlugin extends BuiltinKubeJSClientPlugin {
	@Override
	public void clientInit() {
		super.clientInit();
		NeoForge.EVENT_BUS.addListener(EventPriority.LOW, this::openScreenEvent);
	}

	private void openScreenEvent(ScreenEvent.Opening event) {
		var s = KubeJSClientEventHandler.setScreen(event.getScreen());

		if (s != null && event.getScreen() != s) {
			event.setNewScreen(s);
		}
	}
}
