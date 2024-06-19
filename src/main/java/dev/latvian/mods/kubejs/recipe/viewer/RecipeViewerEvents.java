package dev.latvian.mods.kubejs.recipe.viewer;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.SpecializedEventHandler;

public interface RecipeViewerEvents {
	EventGroup GROUP = EventGroup.of("RecipeViewerEvents");

	SpecializedEventHandler<RecipeViewerEntryType> ADD_ENTRIES = GROUP.common("addEntries", RecipeViewerEntryType.EXTRA, () -> AddEntriesKubeEvent.class).required();
	SpecializedEventHandler<RecipeViewerEntryType> REMOVE_ENTRIES = GROUP.common("removeEntries", RecipeViewerEntryType.EXTRA, () -> RemoveEntriesKubeEvent.class).required();
	SpecializedEventHandler<RecipeViewerEntryType> GROUP_ENTRIES = GROUP.common("groupEntries", RecipeViewerEntryType.EXTRA, () -> GroupEntriesKubeEvent.class).required();
	SpecializedEventHandler<RecipeViewerEntryType> ADD_INFORMATION = GROUP.common("addInformation", RecipeViewerEntryType.EXTRA, () -> AddInformationKubeEvent.class).required();
	SpecializedEventHandler<RecipeViewerEntryType> REGISTER_SUBTYPES = GROUP.common("registerSubtypes", RecipeViewerEntryType.EXTRA, () -> RegisterSubtypesKubeEvent.class).required();
	EventHandler REMOVE_CATEGORIES = GROUP.common("removeCategories", () -> RemoveCategoriesKubeEvent.class);
	EventHandler REMOVE_RECIPES = GROUP.common("removeRecipes", () -> RemoveRecipesKubeEvent.class);
}
