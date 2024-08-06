package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.event.EventGroup;

public interface KGUIEvents {
	EventGroup GROUP = EventGroup.of("KGUIEvents");

	// TargetedEventHandler<String> CREATE = GROUP.client("create", () -> KGUI.class).requiredTarget(EventTargetType.STRING);
}
