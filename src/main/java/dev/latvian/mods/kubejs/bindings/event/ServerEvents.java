package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.command.CommandRegistryKubeEvent;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.recipe.AfterRecipesLoadedKubeEvent;
import dev.latvian.mods.kubejs.recipe.CompostableRecipesKubeEvent;
import dev.latvian.mods.kubejs.recipe.RecipesKubeEvent;
import dev.latvian.mods.kubejs.recipe.special.SpecialRecipeSerializerManager;
import dev.latvian.mods.kubejs.script.data.DataPackKubeEvent;
import dev.latvian.mods.kubejs.server.CommandKubeEvent;
import dev.latvian.mods.kubejs.server.CustomCommandKubeEvent;
import dev.latvian.mods.kubejs.server.ServerKubeEvent;
import dev.latvian.mods.kubejs.server.tag.TagKubeEvent;

public interface ServerEvents {
	EventGroup GROUP = EventGroup.of("ServerEvents");
	EventHandler LOW_DATA = GROUP.server("lowPriorityData", () -> DataPackKubeEvent.class);
	EventHandler HIGH_DATA = GROUP.server("highPriorityData", () -> DataPackKubeEvent.class);
	EventHandler LOADED = GROUP.server("loaded", () -> ServerKubeEvent.class);
	EventHandler UNLOADED = GROUP.server("unloaded", () -> ServerKubeEvent.class);
	EventHandler TICK = GROUP.server("tick", () -> ServerKubeEvent.class);
	EventHandler TAGS = GROUP.server("tags", () -> TagKubeEvent.class).extra(Extra.REQUIRES_REGISTRY);
	EventHandler COMMAND_REGISTRY = GROUP.server("commandRegistry", () -> CommandRegistryKubeEvent.class);
	EventHandler COMMAND = GROUP.server("command", () -> CommandKubeEvent.class).extra(Extra.STRING).hasResult();
	EventHandler CUSTOM_COMMAND = GROUP.server("customCommand", () -> CustomCommandKubeEvent.class).extra(Extra.STRING).hasResult();
	EventHandler RECIPES = GROUP.server("recipes", () -> RecipesKubeEvent.class);
	EventHandler RECIPES_AFTER_LOADED = GROUP.server("afterRecipes", () -> AfterRecipesLoadedKubeEvent.class);
	EventHandler SPECIAL_RECIPES = GROUP.server("specialRecipeSerializers", () -> SpecialRecipeSerializerManager.class);
	EventHandler COMPOSTABLE_RECIPES = GROUP.server("compostableRecipes", () -> CompostableRecipesKubeEvent.class);
}
