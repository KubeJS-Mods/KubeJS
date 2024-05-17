package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;

public interface REIEvents {
	EventGroup GROUP = EventGroup.of("REIEvents");

	EventHandler HIDE = GROUP.client("hide", () -> HideREIKubeEvent.class).extra(Extra.REQUIRES_ID);
	EventHandler ADD = GROUP.client("add", () -> AddREIKubeEvent.class).extra(Extra.REQUIRES_ID);
	EventHandler INFORMATION = GROUP.client("information", () -> InformationREIKubeEvent.class);
	EventHandler REMOVE_CATEGORIES = GROUP.client("removeCategories", () -> RemoveREICategoryKubeEvent.class);
	EventHandler REMOVE_RECIPES = GROUP.client("removeRecipes", () -> RemoveREIRecipeKubeEvent.class);
	EventHandler GROUP_ENTRIES = GROUP.client("groupEntries", () -> GroupREIEntriesKubeEvent.class);
}