package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.kubejs.recipe.viewer.RemoveEntriesKubeEvent;
import dev.latvian.mods.rhino.Context;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;

import java.util.List;
import java.util.function.Predicate;

public class REIRemoveEntriesKubeEvent implements RemoveEntriesKubeEvent {
	private final RecipeViewerEntryType type;
	private final EntryRegistry registry;
	private final List<EntryStack<?>> allEntries;

	public REIRemoveEntriesKubeEvent(RecipeViewerEntryType type, EntryRegistry registry, List<EntryStack<?>> allEntries) {
		this.type = type;
		this.registry = registry;
		this.allEntries = allEntries;
	}

	@Override
	public void remove(Context cx, Object filter) {
		var predicate = (Predicate) type.wrapPredicate(cx, filter);

		for (var entry : allEntries) {
			if (predicate.test(entry.getValue())) {
				registry.removeEntry(entry);
			}
		}
	}
}