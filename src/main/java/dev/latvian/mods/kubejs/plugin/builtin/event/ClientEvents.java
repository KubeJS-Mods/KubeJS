package dev.latvian.mods.kubejs.plugin.builtin.event;

import dev.latvian.mods.kubejs.client.AtlasSpriteRegistryKubeEvent;
import dev.latvian.mods.kubejs.client.BlockEntityRendererRegistryKubeEvent;
import dev.latvian.mods.kubejs.client.ClientPlayerKubeEvent;
import dev.latvian.mods.kubejs.client.DebugInfoKubeEvent;
import dev.latvian.mods.kubejs.client.EntityRendererRegistryKubeEvent;
import dev.latvian.mods.kubejs.client.LangKubeEvent;
import dev.latvian.mods.kubejs.client.MenuScreenRegistryKubeEvent;
import dev.latvian.mods.kubejs.client.ParticleProviderRegistryKubeEvent;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.EventTargetType;
import dev.latvian.mods.kubejs.event.TargetedEventHandler;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.script.data.GeneratedDataStage;
import net.minecraft.resources.ResourceLocation;

public interface ClientEvents {
	EventGroup GROUP = EventGroup.of("ClientEvents");

	TargetedEventHandler<GeneratedDataStage> GENERATE_ASSETS = GROUP.client("generateAssets", () -> KubeAssetGenerator.class).requiredTarget(GeneratedDataStage.TARGET);
	EventHandler BLOCK_ENTITY_RENDERER_REGISTRY = GROUP.startup("blockEntityRendererRegistry", () -> BlockEntityRendererRegistryKubeEvent.class);
	EventHandler ENTITY_RENDERER_REGISTRY = GROUP.startup("entityRendererRegistry", () -> EntityRendererRegistryKubeEvent.class);
	EventHandler MENU_SCREEN_REGISTRY = GROUP.startup("menuScreenRegistry", () -> MenuScreenRegistryKubeEvent.class);
	EventHandler LOGGED_IN = GROUP.client("loggedIn", () -> ClientPlayerKubeEvent.class);
	EventHandler LOGGED_OUT = GROUP.client("loggedOut", () -> ClientPlayerKubeEvent.class);
	EventHandler TICK = GROUP.client("tick", () -> ClientPlayerKubeEvent.class);
	EventHandler DEBUG_LEFT = GROUP.client("leftDebugInfo", () -> DebugInfoKubeEvent.class);
	EventHandler DEBUG_RIGHT = GROUP.client("rightDebugInfo", () -> DebugInfoKubeEvent.class);
	TargetedEventHandler<ResourceLocation> ATLAS_SPRITE_REGISTRY = GROUP.client("atlasSpriteRegistry", () -> AtlasSpriteRegistryKubeEvent.class).requiredTarget(EventTargetType.ID);
	TargetedEventHandler<String> LANG = GROUP.client("lang", () -> LangKubeEvent.class).requiredTarget(EventTargetType.STRING);
	EventHandler PARTICLE_PROVIDER_REGISTRY = GROUP.client("particleProviderRegistry", () -> ParticleProviderRegistryKubeEvent.class);
}
