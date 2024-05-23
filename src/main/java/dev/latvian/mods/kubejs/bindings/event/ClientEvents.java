package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.client.AtlasSpriteRegistryKubeEvent;
import dev.latvian.mods.kubejs.client.ClientInitKubeEvent;
import dev.latvian.mods.kubejs.client.ClientKubeEvent;
import dev.latvian.mods.kubejs.client.DebugInfoKubeEvent;
import dev.latvian.mods.kubejs.client.GenerateClientAssetsKubeEvent;
import dev.latvian.mods.kubejs.client.LangKubeEvent;
import dev.latvian.mods.kubejs.client.painter.screen.PaintScreenKubeEvent;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;

public interface ClientEvents {
	EventGroup GROUP = EventGroup.of("ClientEvents");
	// add low assets
	EventHandler HIGH_ASSETS = GROUP.client("highPriorityAssets", () -> GenerateClientAssetsKubeEvent.class);
	EventHandler INIT = GROUP.startup("init", () -> ClientInitKubeEvent.class);
	EventHandler LOGGED_IN = GROUP.client("loggedIn", () -> ClientKubeEvent.class);
	EventHandler LOGGED_OUT = GROUP.client("loggedOut", () -> ClientKubeEvent.class);
	EventHandler TICK = GROUP.client("tick", () -> ClientKubeEvent.class);
	EventHandler PAINTER_UPDATED = GROUP.client("painterUpdated", () -> ClientKubeEvent.class);
	EventHandler DEBUG_LEFT = GROUP.client("leftDebugInfo", () -> DebugInfoKubeEvent.class);
	EventHandler DEBUG_RIGHT = GROUP.client("rightDebugInfo", () -> DebugInfoKubeEvent.class);
	EventHandler PAINT_SCREEN = GROUP.client("paintScreen", () -> PaintScreenKubeEvent.class);
	EventHandler ATLAS_SPRITE_REGISTRY = GROUP.client("atlasSpriteRegistry", () -> AtlasSpriteRegistryKubeEvent.class).extra(Extra.REQUIRES_ID);
	EventHandler LANG = GROUP.client("lang", () -> LangKubeEvent.class).extra(Extra.REQUIRES_STRING);
}
