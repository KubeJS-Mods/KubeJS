package dev.latvian.mods.kubejs.neoforge;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;

public record NativeEventWrapper(String name, IEventBus eventBus) {
	public Object onEvent(Object eventClass, NativeEventConsumer consumer) {
		if (!(eventClass instanceof CharSequence || eventClass instanceof Class)) {
			throw new RuntimeException("Invalid syntax! " + name + ".onEvent(eventType, function) requires event class and handler");
		} else if (!KubeJS.getStartupScriptManager().firstLoad) {
			ConsoleJS.STARTUP.warn(name + ".onEvent() can't be reloaded! You will have to restart the game for changes to take effect.");
			return null;
		}

		try {
			Class type = eventClass instanceof Class<?> c ? c : Class.forName(eventClass.toString());
			eventBus.addListener(EventPriority.NORMAL, false, type, consumer);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		return null;
	}
}
