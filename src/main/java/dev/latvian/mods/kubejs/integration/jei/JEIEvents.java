package dev.latvian.mods.kubejs.integration.jei;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public interface JEIEvents {
	EventGroup GROUP = EventGroup.of("JEIEvents");

	EventHandler SUBTYPES = GROUP.client("subtypes", () -> JEISubtypesKubeEvent.class);
	EventHandler HIDE_ITEMS = GROUP.client("hideItems", () -> HideJEIKubeEvent.class);
	EventHandler HIDE_FLUIDS = GROUP.client("hideFluids", () -> HideJEIKubeEvent.class);
	EventHandler HIDE_CUSTOM = GROUP.client("hideCustom", () -> HideCustomJEIKubeEvent.class);
	EventHandler REMOVE_CATEGORIES = GROUP.client("removeCategories", () -> RemoveJEICategoriesKubeEvent.class);
	EventHandler REMOVE_RECIPES = GROUP.client("removeRecipes", () -> RemoveJEIRecipesKubeEvent.class);
	EventHandler ADD_ITEMS = GROUP.client("addItems", () -> AddJEIKubeEvent.class);
	EventHandler ADD_FLUIDS = GROUP.client("addFluids", () -> AddJEIKubeEvent.class);
}