package dev.latvian.mods.kubejs.recipe.viewer;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.SpecializedEventHandler;

public interface RecipeViewerEvents {
	EventGroup GROUP = EventGroup.of("RecipeViewerEvents");

	SpecializedEventHandler<RecipeViewerEntryType> ADD_ENTRIES = GROUP.client("addEntries", RecipeViewerEntryType.EXTRA, () -> AddEntriesKubeEvent.class).required();
	SpecializedEventHandler<RecipeViewerEntryType> REMOVE_ENTRIES = GROUP.client("removeEntries", RecipeViewerEntryType.EXTRA, () -> RemoveEntriesKubeEvent.class).required();
	SpecializedEventHandler<RecipeViewerEntryType> GROUP_ENTRIES = GROUP.client("groupEntries", RecipeViewerEntryType.EXTRA, () -> GroupEntriesKubeEvent.class).required();
	SpecializedEventHandler<RecipeViewerEntryType> ADD_INFORMATION = GROUP.client("addInformation", RecipeViewerEntryType.EXTRA, () -> AddInformationKubeEvent.class).required();
	SpecializedEventHandler<RecipeViewerEntryType> REGISTER_SUBTYPES = GROUP.client("registerSubtypes", RecipeViewerEntryType.EXTRA, () -> RegisterSubtypesKubeEvent.class).required();
	EventHandler REMOVE_CATEGORIES = GROUP.client("removeCategories", () -> RemoveCategoriesKubeEvent.class);
	EventHandler REMOVE_RECIPES = GROUP.client("removeRecipes", () -> RemoveRecipesKubeEvent.class);
}
