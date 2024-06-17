package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.recipe.viewer.GroupEntriesKubeEvent;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.rhino.Context;
import me.shedaniel.rei.api.client.registry.entry.CollapsibleEntryRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("UnstableApiUsage")
public class REIGroupEntriesKubeEvent implements GroupEntriesKubeEvent {
	private final RecipeViewerEntryType type;
	private final CollapsibleEntryRegistry registry;

	public REIGroupEntriesKubeEvent(RecipeViewerEntryType type, CollapsibleEntryRegistry registry) {
		this.type = type;
		this.registry = registry;
	}

	@Override
	public RecipeViewerEntryType getType() {
		return type;
	}

	@Override
	public void group(Context cx, ResourceLocation groupId, Component description, Object filter) {
		// WIP registry.group(groupId, description, entries);
	}
}
