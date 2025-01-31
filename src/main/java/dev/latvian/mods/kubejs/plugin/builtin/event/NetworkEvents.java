package dev.latvian.mods.kubejs.plugin.builtin.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventTargetType;
import dev.latvian.mods.kubejs.event.TargetedEventHandler;
import dev.latvian.mods.kubejs.net.NetworkKubeEvent;

public interface NetworkEvents {
	EventGroup GROUP = EventGroup.of("NetworkEvents");

	TargetedEventHandler<String> DATA_RECEIVED = GROUP.common("dataReceived", () -> NetworkKubeEvent.class).hasResult().requiredTarget(EventTargetType.STRING);
}
