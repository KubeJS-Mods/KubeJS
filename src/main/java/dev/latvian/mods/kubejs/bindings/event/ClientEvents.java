package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.client.AtlasSpriteRegistryEventJS;
import dev.latvian.mods.kubejs.client.ClientEventJS;
import dev.latvian.mods.kubejs.client.ClientInitEventJS;
import dev.latvian.mods.kubejs.client.DebugInfoEventJS;
import dev.latvian.mods.kubejs.client.GenerateClientAssetsEventJS;
import dev.latvian.mods.kubejs.client.LangEventJS;
import dev.latvian.mods.kubejs.client.painter.screen.PaintScreenEventJS;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;

public interface ClientEvents {
	EventGroup GROUP = EventGroup.of("ClientEvents");
	// add low assets
	EventHandler HIGH_ASSETS = GROUP.client("highPriorityAssets", () -> GenerateClientAssetsEventJS.class);
	EventHandler INIT = GROUP.startup("init", () -> ClientInitEventJS.class);
	EventHandler LOGGED_IN = GROUP.client("loggedIn", () -> ClientEventJS.class);
	EventHandler LOGGED_OUT = GROUP.client("loggedOut", () -> ClientEventJS.class);
	EventHandler TICK = GROUP.client("tick", () -> ClientEventJS.class);
	EventHandler PAINTER_UPDATED = GROUP.client("painterUpdated", () -> ClientEventJS.class);
	EventHandler DEBUG_LEFT = GROUP.client("leftDebugInfo", () -> DebugInfoEventJS.class);
	EventHandler DEBUG_RIGHT = GROUP.client("rightDebugInfo", () -> DebugInfoEventJS.class);
	EventHandler PAINT_SCREEN = GROUP.client("paintScreen", () -> PaintScreenEventJS.class);
	EventHandler ATLAS_SPRITE_REGISTRY = GROUP.client("atlasSpriteRegistry", () -> AtlasSpriteRegistryEventJS.class).extra(Extra.REQUIRES_ID);
	EventHandler LANG = GROUP.client("lang", () -> LangEventJS.class).extra(Extra.REQUIRES_STRING);
}
