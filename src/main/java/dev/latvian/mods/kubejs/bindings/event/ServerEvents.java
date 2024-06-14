package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.command.CommandRegistryKubeEvent;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.event.SpecializedEventHandler;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.recipe.AfterRecipesLoadedKubeEvent;
import dev.latvian.mods.kubejs.recipe.CompostableRecipesKubeEvent;
import dev.latvian.mods.kubejs.recipe.ModifyCraftingItemKubeEvent;
import dev.latvian.mods.kubejs.recipe.RecipesKubeEvent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeMappingRegistry;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;
import dev.latvian.mods.kubejs.recipe.special.SpecialRecipeSerializerManager;
import dev.latvian.mods.kubejs.script.data.DataPackKubeEvent;
import dev.latvian.mods.kubejs.server.CommandKubeEvent;
import dev.latvian.mods.kubejs.server.CustomCommandKubeEvent;
import dev.latvian.mods.kubejs.server.ServerKubeEvent;
import dev.latvian.mods.kubejs.server.tag.TagKubeEvent;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface ServerEvents {
	EventGroup GROUP = EventGroup.of("ServerEvents");

	EventHandler LOW_DATA = GROUP.server("lowPriorityData", () -> DataPackKubeEvent.class);
	EventHandler HIGH_DATA = GROUP.server("highPriorityData", () -> DataPackKubeEvent.class);
	EventHandler LOADED = GROUP.server("loaded", () -> ServerKubeEvent.class);
	EventHandler UNLOADED = GROUP.server("unloaded", () -> ServerKubeEvent.class);
	EventHandler TICK = GROUP.server("tick", () -> ServerKubeEvent.class);
	SpecializedEventHandler<ResourceKey<Registry<?>>> TAGS = GROUP.server("tags", Extra.REGISTRY, () -> TagKubeEvent.class).required().exceptionHandler(TagKubeEvent.TAG_EVENT_HANDLER);
	EventHandler COMMAND_REGISTRY = GROUP.server("commandRegistry", () -> CommandRegistryKubeEvent.class);
	SpecializedEventHandler<String> COMMAND = GROUP.server("command", Extra.STRING, () -> CommandKubeEvent.class).hasResult();
	SpecializedEventHandler<String> CUSTOM_COMMAND = GROUP.server("customCommand", Extra.STRING, () -> CustomCommandKubeEvent.class).hasResult();
	EventHandler RECIPE_MAPPING_REGISTRY = GROUP.server("recipeMappingRegistry", () -> RecipeMappingRegistry.class);
	EventHandler RECIPE_SCHEMA_REGISTRY = GROUP.server("recipeSchemaRegistry", () -> RecipeSchemaRegistry.class);
	EventHandler RECIPES = GROUP.server("recipes", () -> RecipesKubeEvent.class);
	EventHandler RECIPES_AFTER_LOADED = GROUP.server("afterRecipes", () -> AfterRecipesLoadedKubeEvent.class);
	EventHandler SPECIAL_RECIPES = GROUP.server("specialRecipeSerializers", () -> SpecialRecipeSerializerManager.class);
	EventHandler COMPOSTABLE_RECIPES = GROUP.server("compostableRecipes", () -> CompostableRecipesKubeEvent.class);
	SpecializedEventHandler<String> MODIFY_RECIPE_RESULT = GROUP.server("modifyRecipeResult", Extra.STRING, () -> ModifyCraftingItemKubeEvent.class).required().hasResult(ItemStackJS.TYPE_INFO);
	SpecializedEventHandler<String> MODIFY_RECIPE_INGREDIENT = GROUP.server("modifyRecipeIngredient", Extra.STRING, () -> ModifyCraftingItemKubeEvent.class).required().hasResult(ItemStackJS.TYPE_INFO);
}
