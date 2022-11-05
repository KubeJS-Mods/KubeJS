package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.net.NetworkEventJS;

public interface NetworkEvents {
	EventGroup GROUP = EventGroup.of("NetworkEvents");
	EventHandler FROM_SERVER = GROUP.server("fromServer", () -> NetworkEventJS.class).extra(Extra.REQUIRES_STRING).cancelable();
	EventHandler FROM_CLIENT = GROUP.client("fromClient", () -> NetworkEventJS.class).extra(Extra.REQUIRES_STRING).cancelable();
}
