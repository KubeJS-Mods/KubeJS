package dev.latvian.mods.kubejs.integration.jei;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public interface JEIEvents {
	EventGroup GROUP = EventGroup.of("JEIEvents");

	EventHandler SUBTYPES = GROUP.client("subtypes", () -> JEISubtypesEventJS.class);
	EventHandler HIDE_ITEMS = GROUP.client("hideItems", () -> HideJEIEventJS.class);
	EventHandler HIDE_FLUIDS = GROUP.client("hideFluids", () -> HideJEIEventJS.class);
	EventHandler HIDE_CUSTOM = GROUP.client("hideCustom", () -> HideCustomJEIEventJS.class);
	EventHandler REMOVE_CATEGORIES = GROUP.client("removeCategories", () -> RemoveJEICategoriesEvent.class);
	EventHandler REMOVE_RECIPES = GROUP.client("removeRecipes", () -> RemoveJEIRecipesEvent.class);
	EventHandler ADD_ITEMS = GROUP.client("addItems", () -> AddJEIEventJS.class);
	EventHandler ADD_FLUIDS = GROUP.client("addFluids", () -> AddJEIEventJS.class);
	EventHandler INFORMATION = GROUP.client("information", () -> InformationJEIEventJS.class);

	static void register() {
		GROUP.register();
	}
}