package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.net.NetworkEventJS;

public interface NetworkEvents {
	EventGroup GROUP = EventGroup.of("NetworkEvents");
	EventHandler FROM_SERVER = GROUP.server("fromServer", () -> NetworkEventJS.class).requiresExtraId().cancelable();
	EventHandler FROM_CLIENT = GROUP.client("fromClient", () -> NetworkEventJS.class).requiresExtraId().cancelable();
}
