package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.recipe.viewer.AddEntriesKubeEvent;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.rhino.Context;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.EntryType;

public class REIAddEntriesKubeEvent implements AddEntriesKubeEvent {
	private final RecipeViewerEntryType type;
	private final EntryType entryType;
	private final EntryRegistry registry;

	public REIAddEntriesKubeEvent(RecipeViewerEntryType type, EntryType<?> entryType, EntryRegistry registry) {
		this.type = type;
		this.entryType = entryType;
		this.registry = registry;
	}

	@Override
	public void add(Context cx, Object[] items) {
		for (var item : items) {
			registry.addEntries(EntryStack.of(entryType, type.wrapEntry(cx, item)));
		}
	}
}