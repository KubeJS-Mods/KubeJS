package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.event.StartupEventJS;
import net.minecraft.resources.ResourceLocation;

public interface StartupEvents {
	EventGroup GROUP = EventGroup.of("StartupEvents");
	EventHandler INIT = GROUP.startup("init", () -> StartupEventJS.class);
	EventHandler POST_INIT = GROUP.startup("postInit", () -> StartupEventJS.class);
	EventHandler REGISTRY = GROUP.startup("registry", () -> RegistryObjectBuilderTypes.RegistryEventJS.class).extra(Extra.REQUIRES_ID.validator(o -> KubeJSRegistries.registries().contains((ResourceLocation) o)));
}
