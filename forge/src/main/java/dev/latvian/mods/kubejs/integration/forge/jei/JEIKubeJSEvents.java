package dev.latvian.mods.kubejs.integration.forge.jei;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

/**
 * @author LatvianModder
 */
public interface JEIKubeJSEvents {
	EventGroup GROUP = EventGroup.of("JEIEvents");

	EventHandler SUBTYPES = GROUP.client("subtypes", () -> JEISubtypesEventJS.class).legacy("jei.subtypes");
	EventHandler HIDE_ITEMS = GROUP.client("hideItems", () -> HideJEIEventJS.class).legacy("jei.hide.items");
	EventHandler HIDE_FLUIDS = GROUP.client("hideFluids", () -> HideJEIEventJS.class).legacy("jei.hide.fluids");
	EventHandler HIDE_CUSTOM = GROUP.client("hideCustom", () -> HideJEIEventJS.class).legacy("jei.hide.custom");
	EventHandler REMOVE_CATEGORIES = GROUP.client("removeCategories", () -> RemoveJEICategoriesEvent.class).legacy("jei.remove.categories");
	EventHandler REMOVE_RECIPES = GROUP.client("removeRecipes", () -> RemoveJEIRecipesEvent.class).legacy("jei.remove.recipes");
	EventHandler ADD_ITEMS = GROUP.client("addItems", () -> AddJEIEventJS.class).legacy("jei.add.items");
	EventHandler ADD_FLUIDS = GROUP.client("addFluids", () -> AddJEIEventJS.class).legacy("jei.add.fluids");
	EventHandler INFORMATION = GROUP.client("information", () -> InformationJEIEventJS.class).legacy("jei.information");

	static void register() {
		GROUP.register();
	}
}