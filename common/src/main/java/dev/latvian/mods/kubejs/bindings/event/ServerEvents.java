package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.command.CommandRegistryEventJS;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.loot.BlockLootEventJS;
import dev.latvian.mods.kubejs.loot.ChestLootEventJS;
import dev.latvian.mods.kubejs.loot.EntityLootEventJS;
import dev.latvian.mods.kubejs.loot.FishingLootEventJS;
import dev.latvian.mods.kubejs.loot.GenericLootEventJS;
import dev.latvian.mods.kubejs.loot.GiftLootEventJS;
import dev.latvian.mods.kubejs.recipe.AfterRecipesLoadedEventJS;
import dev.latvian.mods.kubejs.recipe.CompostableRecipesEventJS;
import dev.latvian.mods.kubejs.recipe.RecipeTypeRegistryEventJS;
import dev.latvian.mods.kubejs.recipe.RecipesEventJS;
import dev.latvian.mods.kubejs.recipe.special.SpecialRecipeSerializerManager;
import dev.latvian.mods.kubejs.script.data.DataPackEventJS;
import dev.latvian.mods.kubejs.server.CommandEventJS;
import dev.latvian.mods.kubejs.server.CustomCommandEventJS;
import dev.latvian.mods.kubejs.server.ServerEventJS;
import dev.latvian.mods.kubejs.server.TagEventJS;

public interface ServerEvents {
	EventGroup GROUP = EventGroup.of("ServerEvents");
	EventHandler LOW_DATA = GROUP.server("lowPriorityData", () -> DataPackEventJS.class).legacy("server.datapack.low_priority");
	EventHandler HIGH_DATA = GROUP.server("highPriorityData", () -> DataPackEventJS.class).legacy("server.datapack.high_priority");
	EventHandler LOADED = GROUP.server("loaded", () -> ServerEventJS.class).legacy("server.load");
	EventHandler UNLOADED = GROUP.server("unloaded", () -> ServerEventJS.class).legacy("server.unload");
	EventHandler TICK = GROUP.server("tick", () -> ServerEventJS.class).legacy("server.tick");
	EventHandler TAGS = GROUP.server("tags", () -> TagEventJS.class).requiresNamespacedExtraId();
	EventHandler COMMAND_REGISTRY = GROUP.server("commandRegistry", () -> CommandRegistryEventJS.class).legacy("command.registry");
	EventHandler COMMAND = GROUP.server("command", () -> CommandEventJS.class).supportsExtraId().cancelable().legacy("command.run");
	EventHandler CUSTOM_COMMAND = GROUP.server("customCommand", () -> CustomCommandEventJS.class).supportsExtraId().cancelable().legacy("server.custom_command");
	EventHandler RECIPES = GROUP.server("recipes", () -> RecipesEventJS.class).legacy("recipes");
	EventHandler RECIPES_AFTER_LOADED = GROUP.server("afterRecipes", () -> AfterRecipesLoadedEventJS.class).legacy("recipes.after_loaded");
	EventHandler SPECIAL_RECIPES = GROUP.server("specialRecipeSerializers", () -> SpecialRecipeSerializerManager.class).legacy("recipes.serializer.special.flag");
	EventHandler COMPOSTABLE = GROUP.server("compostables", () -> CompostableRecipesEventJS.class).legacy("recipes.compostables");
	EventHandler RECIPE_TYPE_REGISTRY = GROUP.server("recipeTypeRegistry", () -> RecipeTypeRegistryEventJS.class).legacy("recipes.type_registry");
	EventHandler GENERIC_LOOT_TABLES = GROUP.server("genericLootTables", () -> GenericLootEventJS.class).legacy("generic.loot_tables");
	EventHandler BLOCK_LOOT_TABLES = GROUP.server("blockLootTables", () -> BlockLootEventJS.class).legacy("block.loot_tables");
	EventHandler ENTITY_LOOT_TABLES = GROUP.server("entityLootTables", () -> EntityLootEventJS.class).legacy("entity.loot_tables");
	EventHandler GIFT_LOOT_TABLES = GROUP.server("giftLootTables", () -> GiftLootEventJS.class).legacy("gift.loot_tables");
	EventHandler FISHING_LOOT_TABLES = GROUP.server("fishingLootTables", () -> FishingLootEventJS.class).legacy("fishing.loot_tables");
	EventHandler CHEST_LOOT_TABLES = GROUP.server("chestLootTables", () -> ChestLootEventJS.class).legacy("chest.loot_tables");
}
