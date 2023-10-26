package dev.latvian.mods.kubejs.forge;

import dev.latvian.mods.kubejs.client.BuiltinKubeJSClientPlugin;
import dev.latvian.mods.kubejs.client.KubeJSClientEventHandler;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

public class BuiltinKubeJSForgeClientPlugin extends BuiltinKubeJSClientPlugin {
	@Override
	public void clientInit() {
		super.clientInit();
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, this::openScreenEvent);
	}

	private void openScreenEvent(ScreenEvent.Opening event) {
		var s = KubeJSClientEventHandler.setScreen(event.getScreen());

		if (s != null && event.getScreen() != s) {
			event.setNewScreen(s);
		}
	}
}
