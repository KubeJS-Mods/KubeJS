package dev.latvian.mods.kubejs.plugin.builtin.event;

import dev.latvian.mods.kubejs.command.CommandRegistryKubeEvent;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.EventTargetType;
import dev.latvian.mods.kubejs.event.TargetedEventHandler;
import dev.latvian.mods.kubejs.generator.KubeDataGenerator;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import dev.latvian.mods.kubejs.recipe.AfterRecipesLoadedKubeEvent;
import dev.latvian.mods.kubejs.recipe.CompostableRecipesKubeEvent;
import dev.latvian.mods.kubejs.recipe.ModifyCraftingItemKubeEvent;
import dev.latvian.mods.kubejs.recipe.RecipesKubeEvent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeMappingRegistry;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;
import dev.latvian.mods.kubejs.recipe.special.SpecialRecipeSerializerManager;
import dev.latvian.mods.kubejs.registry.ServerRegistryKubeEvent;
import dev.latvian.mods.kubejs.script.data.GeneratedDataStage;
import dev.latvian.mods.kubejs.server.BasicCommandKubeEvent;
import dev.latvian.mods.kubejs.server.CommandKubeEvent;
import dev.latvian.mods.kubejs.server.ServerKubeEvent;
import dev.latvian.mods.kubejs.server.tag.TagKubeEvent;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface ServerEvents {
	EventGroup GROUP = EventGroup.of("ServerEvents");

	TargetedEventHandler<ResourceKey<Registry<?>>> REGISTRY = GROUP.server("registry", () -> ServerRegistryKubeEvent.class).requiredTarget(EventTargetType.REGISTRY);
	TargetedEventHandler<GeneratedDataStage> GENERATE_DATA = GROUP.server("generateData", () -> KubeDataGenerator.class).requiredTarget(GeneratedDataStage.TARGET);
	EventHandler LOADED = GROUP.server("loaded", () -> ServerKubeEvent.class);
	EventHandler UNLOADED = GROUP.server("unloaded", () -> ServerKubeEvent.class);
	EventHandler TICK = GROUP.server("tick", () -> ServerKubeEvent.class);
	TargetedEventHandler<ResourceKey<Registry<?>>> TAGS = GROUP.server("tags", () -> TagKubeEvent.class).exceptionHandler(TagKubeEvent.TAG_EVENT_HANDLER).requiredTarget(EventTargetType.REGISTRY);
	EventHandler COMMAND_REGISTRY = GROUP.server("commandRegistry", () -> CommandRegistryKubeEvent.class);
	TargetedEventHandler<String> COMMAND = GROUP.server("command", () -> CommandKubeEvent.class).hasResult().supportsTarget(EventTargetType.STRING);
	TargetedEventHandler<String> BASIC_COMMAND = GROUP.server("basicCommand", () -> BasicCommandKubeEvent.class).hasResult().requiredTarget(EventTargetType.STRING);
	TargetedEventHandler<String> BASIC_PUBLIC_COMMAND = GROUP.server("basicPublicCommand", () -> BasicCommandKubeEvent.class).hasResult().requiredTarget(EventTargetType.STRING);
	EventHandler RECIPE_MAPPING_REGISTRY = GROUP.server("recipeMappingRegistry", () -> RecipeMappingRegistry.class);
	EventHandler RECIPE_SCHEMA_REGISTRY = GROUP.server("recipeSchemaRegistry", () -> RecipeSchemaRegistry.class);
	EventHandler RECIPES = GROUP.server("recipes", () -> RecipesKubeEvent.class);
	EventHandler RECIPES_AFTER_LOADED = GROUP.server("afterRecipes", () -> AfterRecipesLoadedKubeEvent.class);
	EventHandler SPECIAL_RECIPES = GROUP.server("specialRecipeSerializers", () -> SpecialRecipeSerializerManager.class);
	EventHandler COMPOSTABLE_RECIPES = GROUP.server("compostableRecipes", () -> CompostableRecipesKubeEvent.class);
	TargetedEventHandler<String> MODIFY_RECIPE_RESULT = GROUP.server("modifyRecipeResult", () -> ModifyCraftingItemKubeEvent.class).hasResult(ItemWrapper.TYPE_INFO).requiredTarget(EventTargetType.STRING);
	TargetedEventHandler<String> MODIFY_RECIPE_INGREDIENT = GROUP.server("modifyRecipeIngredient", () -> ModifyCraftingItemKubeEvent.class).hasResult(ItemWrapper.TYPE_INFO).requiredTarget(EventTargetType.STRING);
}
