package dev.latvian.mods.kubejs.forge;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

public record ForgeEventWrapper(String name, IEventBus eventBus) {
	public Object onEvent(Object eventClass, ForgeEventConsumer consumer) {
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

	public Object onGenericEvent(Object eventClass, Object genericClass, GenericForgeEventConsumer consumer) {
		if (!(eventClass instanceof CharSequence || eventClass instanceof Class) || !(genericClass instanceof CharSequence || genericClass instanceof Class)) {
			throw new RuntimeException("Invalid syntax! " + name + ".onGenericEvent(eventType, genericType, function) requires event class, generic class and handler");
		} else if (!KubeJS.getStartupScriptManager().firstLoad) {
			ConsoleJS.STARTUP.warn(name + ".onGenericEvent() can't be reloaded! You will have to restart the game for changes to take effect.");
			return null;
		}

		try {
			Class type = eventClass instanceof Class<?> c ? c : Class.forName(eventClass.toString());
			Class genericType = genericClass instanceof Class<?> c ? c : Class.forName(genericClass.toString());
			eventBus.addGenericListener(genericType, EventPriority.NORMAL, false, type, consumer);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		return null;
	}
}
