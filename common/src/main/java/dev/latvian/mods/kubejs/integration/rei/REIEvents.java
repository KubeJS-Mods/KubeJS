package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

/**
 * @author shedaniel
 */
public interface REIEvents {
	EventGroup GROUP = EventGroup.of("REIEvents");

	EventHandler HIDE = GROUP.client("hide", () -> HideREIEventJS.class).requiresNamespacedExtraId();
	EventHandler ADD = GROUP.client("add", () -> AddREIEventJS.class).requiresNamespacedExtraId();
	EventHandler INFORMATION = GROUP.client("information", () -> InformationREIEventJS.class);
	EventHandler REMOVE_CATEGORIES = GROUP.client("removeCategories", () -> RemoveREICategoryEventJS.class);
	EventHandler GROUP_ENTRIES = GROUP.client("groupEntries", () -> GroupREIEntriesEventJS.class);

	static void register() {
		GROUP.register();
	}
}