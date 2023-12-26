package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.event.StartupEventJS;
import dev.latvian.mods.kubejs.item.creativetab.CreativeTabEvent;
import dev.latvian.mods.kubejs.recipe.RecipeSchemaRegistryEventJS;
import dev.latvian.mods.kubejs.registry.RegistryEventJS;

public interface StartupEvents {
	EventGroup GROUP = EventGroup.of("StartupEvents");

	EventHandler INIT = GROUP.startup("init", () -> StartupEventJS.class);
	EventHandler POST_INIT = GROUP.startup("postInit", () -> StartupEventJS.class);
	EventHandler REGISTRY = GROUP.startup("registry", () -> RegistryEventJS.class).extra(Extra.REQUIRES_REGISTRY);
	EventHandler RECIPE_SCHEMA_REGISTRY = GROUP.startup("recipeSchemaRegistry", () -> RecipeSchemaRegistryEventJS.class);
	EventHandler MODIFY_CREATIVE_TAB = GROUP.startup("modifyCreativeTab", () -> CreativeTabEvent.class).extra(Extra.REQUIRES_ID);
}
