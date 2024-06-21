package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.kubejs.recipe.viewer.RemoveEntriesKubeEvent;
import dev.latvian.mods.rhino.Context;
import me.shedaniel.rei.api.client.entry.filtering.base.BasicFilteringRule;
import me.shedaniel.rei.api.common.entry.EntryStack;

import java.util.List;
import java.util.function.Predicate;

public class REIRemoveEntriesCompletelyKubeEvent implements RemoveEntriesKubeEvent {
	private final RecipeViewerEntryType type;
	private final List<EntryStack<?>> allEntries;
	private final BasicFilteringRule<?> rule;
	private List<Object> allValues;

	public REIRemoveEntriesCompletelyKubeEvent(RecipeViewerEntryType type, List<EntryStack<?>> allEntries, BasicFilteringRule<?> rule) {
		this.type = type;
		this.allEntries = allEntries;
		this.rule = rule;
	}

	@Override
	public void remove(Context cx, Object filter) {
		var predicate = (Predicate) type.wrapPredicate(cx, filter);
		rule.hide(allEntries.stream().filter(e -> predicate.test(e.getValue())).toList());
	}

	@Override
	public List<Object> getAllEntryValues() {
		if (allValues == null) {
			allValues = List.copyOf(allEntries.stream().map(EntryStack::getValue).toList());
		}

		return allValues;
	}
}