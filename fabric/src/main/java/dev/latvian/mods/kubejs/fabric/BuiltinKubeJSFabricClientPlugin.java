package dev.latvian.mods.kubejs.fabric;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.latvian.mods.kubejs.client.BuiltinKubeJSClientPlugin;
import dev.latvian.mods.kubejs.client.KubeJSClientEventHandler;
import net.minecraft.client.gui.screens.Screen;

public class BuiltinKubeJSFabricClientPlugin extends BuiltinKubeJSClientPlugin {
	@Override
	public void clientInit() {
		super.clientInit();
		ClientGuiEvent.SET_SCREEN.register(this::openScreenEvent);
	}

	private CompoundEventResult<Screen> openScreenEvent(Screen screen) {
		var s = KubeJSClientEventHandler.setScreen(screen);
		return s == screen ? CompoundEventResult.pass() : CompoundEventResult.interruptTrue(s);
	}
}
