package dev.latvian.mods.kubejs.recipe.viewer;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.EventTargetType;
import dev.latvian.mods.kubejs.event.TargetedEventHandler;

public interface RecipeViewerEvents {
	EventGroup GROUP = EventGroup.of("RecipeViewerEvents");
	EventTargetType<RecipeViewerEntryType> TARGET = EventTargetType.create(RecipeViewerEntryType.class).transformer(RecipeViewerEntryType::fromString).identity();

	TargetedEventHandler<RecipeViewerEntryType> ADD_ENTRIES = GROUP.common("addEntries", () -> AddEntriesKubeEvent.class).requiredTarget(TARGET);
	TargetedEventHandler<RecipeViewerEntryType> REMOVE_ENTRIES = GROUP.common("removeEntries", () -> RemoveEntriesKubeEvent.class).requiredTarget(TARGET);
	TargetedEventHandler<RecipeViewerEntryType> REMOVE_ENTRIES_COMPLETELY = GROUP.common("removeEntriesCompletely", () -> RemoveEntriesKubeEvent.class).requiredTarget(TARGET);
	TargetedEventHandler<RecipeViewerEntryType> GROUP_ENTRIES = GROUP.common("groupEntries", () -> GroupEntriesKubeEvent.class).requiredTarget(TARGET);
	TargetedEventHandler<RecipeViewerEntryType> ADD_INFORMATION = GROUP.common("addInformation", () -> AddInformationKubeEvent.class).requiredTarget(TARGET);
	TargetedEventHandler<RecipeViewerEntryType> REGISTER_SUBTYPES = GROUP.common("registerSubtypes", () -> RegisterSubtypesKubeEvent.class).requiredTarget(TARGET);
	EventHandler REMOVE_CATEGORIES = GROUP.common("removeCategories", () -> RemoveCategoriesKubeEvent.class);
	EventHandler REMOVE_RECIPES = GROUP.common("removeRecipes", () -> RemoveRecipesKubeEvent.class);
}
