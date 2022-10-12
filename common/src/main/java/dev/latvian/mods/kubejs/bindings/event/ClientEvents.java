package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.client.ClientEventJS;
import dev.latvian.mods.kubejs.client.DebugInfoEventJS;
import dev.latvian.mods.kubejs.client.GenerateClientAssetsEventJS;
import dev.latvian.mods.kubejs.client.painter.screen.PaintScreenEventJS;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public interface ClientEvents {
	EventGroup GROUP = EventGroup.of("ClientEvents");
	// add low assets
	EventHandler HIGH_ASSETS = GROUP.client("highPriorityAssets", () -> GenerateClientAssetsEventJS.class);
	EventHandler INIT = GROUP.client("init", () -> ClientEventJS.class);
	EventHandler LOGGED_IN = GROUP.client("loggedIn", () -> ClientEventJS.class);
	EventHandler LOGGED_OUT = GROUP.client("loggedOut", () -> ClientEventJS.class);
	EventHandler TICK = GROUP.client("tick", () -> ClientEventJS.class);
	EventHandler PAINTER_UPDATED = GROUP.client("painterUpdated", () -> ClientEventJS.class);
	EventHandler DEBUG_LEFT = GROUP.client("leftDebugInfo", () -> DebugInfoEventJS.class);
	EventHandler DEBUG_RIGHT = GROUP.client("rightDebugInfo", () -> DebugInfoEventJS.class);
	EventHandler PAINT_SCREEN = GROUP.client("paintScreen", () -> PaintScreenEventJS.class);
}
