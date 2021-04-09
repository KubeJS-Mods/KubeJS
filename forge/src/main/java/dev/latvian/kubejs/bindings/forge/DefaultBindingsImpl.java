package dev.latvian.kubejs.bindings.forge;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.ScriptType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

public class DefaultBindingsImpl {
	public static void registerPlatformEvents(BindingsEvent event) {
		if (event.type == ScriptType.STARTUP) {
			event.addFunction("onForgeEvent", args -> onPlatformEvent(event, args), null, KubeJSForgeEventHandlerWrapper.class);
		}
	}

	public static Object onPlatformEvent(BindingsEvent event, Object[] args) {
		if (args.length < 2 || !(args[0] instanceof CharSequence)) {
			throw new RuntimeException("Invalid syntax! onPlatformEvent(string, function) required event class and handler");
		} else if (!KubeJS.startupScriptManager.firstLoad) {
			ScriptType.STARTUP.console.warn("onPlatformEvent() can't be reloaded! You will have to restart the game for changes to take effect.");
			return null;
		}

		try {
			Class type = Class.forName(args[0].toString());
			KubeJSForgeEventHandlerWrapper handler = (KubeJSForgeEventHandlerWrapper) args[1];
			MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, type, handler);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		return null;
	}
}
