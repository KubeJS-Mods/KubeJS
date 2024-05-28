package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.event.SpecializedEventHandler;
import net.minecraft.resources.ResourceLocation;

public interface REIEvents {
	EventGroup GROUP = EventGroup.of("REIEvents");

	SpecializedEventHandler<ResourceLocation> HIDE = GROUP.client("hide", Extra.ID, () -> HideREIKubeEvent.class).required();
	SpecializedEventHandler<ResourceLocation> ADD = GROUP.client("add", Extra.ID, () -> AddREIKubeEvent.class).required();
	EventHandler INFORMATION = GROUP.client("information", () -> InformationREIKubeEvent.class);
	EventHandler REMOVE_CATEGORIES = GROUP.client("removeCategories", () -> RemoveREICategoryKubeEvent.class);
	EventHandler REMOVE_RECIPES = GROUP.client("removeRecipes", () -> RemoveREIRecipeKubeEvent.class);
	EventHandler GROUP_ENTRIES = GROUP.client("groupEntries", () -> GroupREIEntriesKubeEvent.class);
}