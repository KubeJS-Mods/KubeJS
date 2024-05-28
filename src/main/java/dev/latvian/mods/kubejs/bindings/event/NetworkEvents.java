package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.event.SpecializedEventHandler;
import dev.latvian.mods.kubejs.net.NetworkKubeEvent;

public interface NetworkEvents {
	EventGroup GROUP = EventGroup.of("NetworkEvents");

	SpecializedEventHandler<String> DATA_RECEIVED = GROUP.common("dataReceived", Extra.STRING, () -> NetworkKubeEvent.class).required().hasResult();
}
