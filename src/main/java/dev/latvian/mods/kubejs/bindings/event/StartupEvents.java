package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.event.KubeStartupEvent;
import dev.latvian.mods.kubejs.item.creativetab.CreativeTabKubeEvent;
import dev.latvian.mods.kubejs.recipe.RecipeSchemaRegistryKubeEvent;
import dev.latvian.mods.kubejs.registry.RegistryKubeEvent;

public interface StartupEvents {
	EventGroup GROUP = EventGroup.of("StartupEvents");

	EventHandler INIT = GROUP.startup("init", () -> KubeStartupEvent.class);
	EventHandler POST_INIT = GROUP.startup("postInit", () -> KubeStartupEvent.class);
	EventHandler REGISTRY = GROUP.startup("registry", () -> RegistryKubeEvent.class).extra(Extra.REQUIRES_REGISTRY);
	EventHandler RECIPE_SCHEMA_REGISTRY = GROUP.startup("recipeSchemaRegistry", () -> RecipeSchemaRegistryKubeEvent.class);
	EventHandler MODIFY_CREATIVE_TAB = GROUP.startup("modifyCreativeTab", () -> CreativeTabKubeEvent.class).extra(Extra.REQUIRES_ID);
}
