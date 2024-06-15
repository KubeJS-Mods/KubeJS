package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.client.AtlasSpriteRegistryKubeEvent;
import dev.latvian.mods.kubejs.client.ClientInitKubeEvent;
import dev.latvian.mods.kubejs.client.ClientKubeEvent;
import dev.latvian.mods.kubejs.client.DebugInfoKubeEvent;
import dev.latvian.mods.kubejs.client.GenerateClientAssetsKubeEvent;
import dev.latvian.mods.kubejs.client.LangKubeEvent;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.event.SpecializedEventHandler;
import net.minecraft.resources.ResourceLocation;

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
	SpecializedEventHandler<ResourceLocation> ATLAS_SPRITE_REGISTRY = GROUP.client("atlasSpriteRegistry", Extra.ID, () -> AtlasSpriteRegistryKubeEvent.class).required();
	SpecializedEventHandler<String> LANG = GROUP.client("lang", Extra.STRING, () -> LangKubeEvent.class).required();
}
