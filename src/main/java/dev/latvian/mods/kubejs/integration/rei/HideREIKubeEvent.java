package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.helpers.IngredientHelper;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Context;
import me.shedaniel.rei.api.client.entry.filtering.base.BasicFilteringRule;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class HideREIKubeEvent<T, C> implements KubeEvent {
	private final EntryRegistry registry;
	private final EntryWrapper<T, C> entryWrapper;
	private final BasicFilteringRule<?> rule;
	private final List<EntryStack<T>> allEntries;
	private List<T> allValues;
	private final List<Predicate<T>> hide;
	private Predicate<T>[] hideArray;
	private final Collection<EntryStack<T>> hiddenNoFilter;

	public HideREIKubeEvent(EntryRegistry registry, EntryWrapper<T, C> entryWrapper, BasicFilteringRule<?> rule, EntryStack<?>[] allEntries0) {
		this.registry = registry;
		this.entryWrapper = entryWrapper;
		this.rule = rule;
		this.allEntries = new ArrayList<>();

		for (var entry : allEntries0) {
			if (entry.getType() == entryWrapper.type()) {
				this.allEntries.add(entry.cast());
			}
		}

		this.allValues = null;
		this.hide = new ArrayList<>();
		this.hiddenNoFilter = new ArrayList<>();
	}

	public List<T> getAllEntryValues() {
		if (allValues == null) {
			allValues = allEntries.stream().map(EntryStack::getValue).toList();
		}

		return allValues;
	}

	public void hide(Context cx, Object entries, @Nullable Object except) {
		if (hide != UtilsJS.ALWAYS_TRUE) {
			var filter = entryWrapper.filter(cx, entries);

			if (except != null) {
				filter = filter.and(entryWrapper.filter(cx, except).negate());
			}

			hide.add(filter);
		}
	}

	public void hide(Context cx, Object entries) {
		hide(cx, entries, null);
	}

	public void hideNoFilter(Context cx, Object o) {
		hiddenNoFilter.addAll(entryWrapper.entryList(cx, o));
	}

	public void hideAll(Context cx, @Nullable Object except) {
		hide(cx, IngredientHelper.get().wildcard(), except);
	}

	public void hideAll(Context cx) {
		hideAll(cx, null);
	}

	@Override
	public void afterPosted(EventResult result) {
		if (hide != null) {
			hideArray = hide.toArray(new Predicate[0]);
			rule.hide(allEntries.stream().filter(this::testEntry).toList());
		}

		if (!hiddenNoFilter.isEmpty()) {
			hiddenNoFilter.forEach(registry::removeEntry);
		}
	}

	private boolean testEntry(EntryStack<T> e) {
		for (var h : hideArray) {
			if (h.test(e.getValue())) {
				return true;
			}
		}

		return false;
	}
}