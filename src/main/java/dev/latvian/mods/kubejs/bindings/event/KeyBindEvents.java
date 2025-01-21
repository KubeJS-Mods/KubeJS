package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.client.KeybindRegistryKubeEvent;
import dev.latvian.mods.kubejs.client.KubeJSKeybinds;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.TargetedEventHandler;

public interface KeyBindEvents {
	EventGroup GROUP = EventGroup.of("KeyBindEvents");

	EventHandler REGISTRY = GROUP.startup("registry", () -> KeybindRegistryKubeEvent.class);
	TargetedEventHandler<KubeJSKeybinds.KubeKey> PRESSED = GROUP.client("pressed", () -> KubeJSKeybinds.KeyEvent.class).requiredTarget(KubeJSKeybinds.TARGET);
	TargetedEventHandler<KubeJSKeybinds.KubeKey> RELEASED = GROUP.client("released", () -> KubeJSKeybinds.TickingKeyEvent.class).requiredTarget(KubeJSKeybinds.TARGET);
	TargetedEventHandler<KubeJSKeybinds.KubeKey> TICK = GROUP.client("tick", () -> KubeJSKeybinds.TickingKeyEvent.class).requiredTarget(KubeJSKeybinds.TARGET);
}
