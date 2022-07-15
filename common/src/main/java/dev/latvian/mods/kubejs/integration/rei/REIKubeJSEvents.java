package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

/**
 * @author shedaniel
 */
public interface REIKubeJSEvents {
	EventGroup GROUP = EventGroup.of("REIEvents");

	EventHandler HIDE = GROUP.client("hide", () -> HideREIEventJS.class).requiresNamespacedExtraId().legacy("rei.hide");
	EventHandler ADD = GROUP.client("add", () -> AddREIEventJS.class).requiresNamespacedExtraId().legacy("rei.add");
	EventHandler INFORMATION = GROUP.client("information", () -> InformationREIEventJS.class).legacy("rei.information");
	EventHandler REMOVE_CATEGORIES = GROUP.client("removeCategories", () -> RemoveREICategoryEventJS.class).legacy("rei.remove.categories");

	static void register() {
		GROUP.register();
	}
}