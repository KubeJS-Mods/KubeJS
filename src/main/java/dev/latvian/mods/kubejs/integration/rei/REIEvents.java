package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;

public interface REIEvents {
	EventGroup GROUP = EventGroup.of("REIEvents");

	EventHandler HIDE = GROUP.client("hide", () -> HideREIEventJS.class).extra(Extra.REQUIRES_ID);
	EventHandler ADD = GROUP.client("add", () -> AddREIEventJS.class).extra(Extra.REQUIRES_ID);
	EventHandler INFORMATION = GROUP.client("information", () -> InformationREIEventJS.class);
	EventHandler REMOVE_CATEGORIES = GROUP.client("removeCategories", () -> RemoveREICategoryEventJS.class);
	EventHandler REMOVE_RECIPES = GROUP.client("removeRecipes", () -> RemoveREIRecipeEventJS.class);
	EventHandler GROUP_ENTRIES = GROUP.client("groupEntries", () -> GroupREIEntriesEventJS.class);
}