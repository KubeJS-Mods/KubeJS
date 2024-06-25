package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.EventTargetType;
import dev.latvian.mods.kubejs.event.KubeStartupEvent;
import dev.latvian.mods.kubejs.event.TargetedEventHandler;
import dev.latvian.mods.kubejs.item.creativetab.CreativeTabKubeEvent;
import dev.latvian.mods.kubejs.registry.RegistryKubeEvent;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public interface StartupEvents {
	EventGroup GROUP = EventGroup.of("StartupEvents");

	EventHandler INIT = GROUP.startup("init", () -> KubeStartupEvent.class);
	EventHandler POST_INIT = GROUP.startup("postInit", () -> KubeStartupEvent.class);
	TargetedEventHandler<ResourceKey<Registry<?>>> REGISTRY = GROUP.startup("registry", () -> RegistryKubeEvent.class).requiredTarget(EventTargetType.REGISTRY);
	TargetedEventHandler<ResourceLocation> MODIFY_CREATIVE_TAB = GROUP.startup("modifyCreativeTab", () -> CreativeTabKubeEvent.class).requiredTarget(EventTargetType.ID);
}
