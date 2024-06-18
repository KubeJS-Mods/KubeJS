package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.kubejs.recipe.viewer.RemoveEntriesKubeEvent;
import dev.latvian.mods.rhino.Context;
import me.shedaniel.rei.api.client.entry.filtering.base.BasicFilteringRule;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;

import java.util.Arrays;
import java.util.function.Predicate;

public class REIRemoveEntriesKubeEvent implements RemoveEntriesKubeEvent {
	private final RecipeViewerEntryType type;
	private final EntryRegistry registry;
	private final EntryStack<?>[] allEntries;
	private final BasicFilteringRule<?> rule;
	private Object[] allValues;

	public REIRemoveEntriesKubeEvent(RecipeViewerEntryType type, EntryRegistry registry, EntryStack[] allEntries, BasicFilteringRule<?> rule) {
		this.type = type;
		this.registry = registry;
		this.allEntries = allEntries;
		this.rule = rule;
	}

	@Override
	public void remove(Context cx, Object filter) {
		var predicate = (Predicate) type.wrapPredicate(cx, filter);
		rule.hide(Arrays.stream(allEntries).filter(e -> predicate.test(e.getValue())).toList());
	}

	@Override
	public void removeDirectly(Context cx, Object filter) {
		var predicate = (Predicate) type.wrapPredicate(cx, filter);

		for (var entry : allEntries) {
			if (predicate.test(entry.getValue())) {
				registry.removeEntry(entry);
			}
		}
	}

	@Override
	public Object[] getAllEntryValues() {
		if (allValues == null) {
			allValues = new Object[allEntries.length];

			for (int i = 0; i < allEntries.length; i++) {
				allValues[i] = allEntries[i].getValue();
			}
		}

		return allValues;
	}
}