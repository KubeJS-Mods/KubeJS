package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.client.AtlasSpriteRegistryKubeEvent;
import dev.latvian.mods.kubejs.client.BlockEntityRendererRegistryKubeEvent;
import dev.latvian.mods.kubejs.client.ClientPlayerKubeEvent;
import dev.latvian.mods.kubejs.client.DebugInfoKubeEvent;
import dev.latvian.mods.kubejs.client.EntityRendererRegistryKubeEvent;
import dev.latvian.mods.kubejs.client.GenerateClientAssetsKubeEvent;
import dev.latvian.mods.kubejs.client.LangKubeEvent;
import dev.latvian.mods.kubejs.client.MenuScreenRegistryKubeEvent;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.event.SpecializedEventHandler;
import net.minecraft.resources.ResourceLocation;

public interface ClientEvents {
	EventGroup GROUP = EventGroup.of("ClientEvents");

	// add low assets
	EventHandler HIGH_ASSETS = GROUP.client("highPriorityAssets", () -> GenerateClientAssetsKubeEvent.class);
	EventHandler ENTITY_RENDERER_REGISTRY = GROUP.startup("blockEntityRendererRegistry", () -> BlockEntityRendererRegistryKubeEvent.class);
	EventHandler BLOCK_ENTITY_RENDERER_REGISTRY = GROUP.startup("entityRendererRegistry", () -> EntityRendererRegistryKubeEvent.class);
	EventHandler MENU_SCREEN_REGISTRY = GROUP.startup("menuScreenRegistry", () -> MenuScreenRegistryKubeEvent.class);
	EventHandler LOGGED_IN = GROUP.client("loggedIn", () -> ClientPlayerKubeEvent.class);
	EventHandler LOGGED_OUT = GROUP.client("loggedOut", () -> ClientPlayerKubeEvent.class);
	EventHandler TICK = GROUP.client("tick", () -> ClientPlayerKubeEvent.class);
	EventHandler DEBUG_LEFT = GROUP.client("leftDebugInfo", () -> DebugInfoKubeEvent.class);
	EventHandler DEBUG_RIGHT = GROUP.client("rightDebugInfo", () -> DebugInfoKubeEvent.class);
	SpecializedEventHandler<ResourceLocation> ATLAS_SPRITE_REGISTRY = GROUP.client("atlasSpriteRegistry", Extra.ID, () -> AtlasSpriteRegistryKubeEvent.class).required();
	SpecializedEventHandler<String> LANG = GROUP.client("lang", Extra.STRING, () -> LangKubeEvent.class).required();
}
