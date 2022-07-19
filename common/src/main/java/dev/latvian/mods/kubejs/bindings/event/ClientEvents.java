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
	EventHandler HIGH_ASSETS = GROUP.client("highPriorityAssets", () -> GenerateClientAssetsEventJS.class).legacy("client.generate_assets");
	EventHandler INIT = GROUP.client("init", () -> ClientEventJS.class).legacy("client.init");
	EventHandler LOGGED_IN = GROUP.client("loggedIn", () -> ClientEventJS.class).legacy("client.logged_in");
	EventHandler LOGGED_OUT = GROUP.client("loggedOut", () -> ClientEventJS.class).legacy("client.logged_out");
	EventHandler TICK = GROUP.client("tick", () -> ClientEventJS.class).legacy("client.tick");
	EventHandler PAINTER_UPDATED = GROUP.client("painterUpdated", () -> ClientEventJS.class).legacy("client.painter_updated");
	EventHandler DEBUG_LEFT = GROUP.client("leftDebugInfo", () -> DebugInfoEventJS.class).legacy("client.debug_info.left");
	EventHandler DEBUG_RIGHT = GROUP.client("rightDebugInfo", () -> DebugInfoEventJS.class).legacy("client.debug_info.right");
	EventHandler PAINT_SCREEN = GROUP.client("paintScreen", () -> PaintScreenEventJS.class).legacy("client.paint_screen");
}
