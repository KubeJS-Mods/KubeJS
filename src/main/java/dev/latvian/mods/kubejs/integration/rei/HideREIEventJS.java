package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.helpers.IngredientHelper;
import dev.latvian.mods.kubejs.util.UtilsJS;
import me.shedaniel.rei.api.client.entry.filtering.base.BasicFilteringRule;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class HideREIEventJS<T, C> extends EventJS {
	private final EntryRegistry registry;
	private final EntryWrapper<T, C> entryWrapper;
	private final BasicFilteringRule<?> rule;
	private final List<EntryStack<T>> allEntries;
	private List<T> allValues;
	private final List<Predicate<T>> hide;
	private Predicate<T>[] hideArray;
	private final Collection<EntryStack<T>> hiddenNoFilter;

	public HideREIEventJS(EntryRegistry registry, EntryWrapper<T, C> entryWrapper, BasicFilteringRule<?> rule, EntryStack<?>[] allEntries0) {
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

	public void hide(Object entries, @Nullable Object except) {
		if (hide != UtilsJS.ALWAYS_TRUE) {
			var filter = entryWrapper.filter(entries);

			if (except != null) {
				filter = filter.and(entryWrapper.filter(except).negate());
			}

			hide.add(filter);
		}
	}

	public void hide(Object entries) {
		hide(entries, null);
	}

	public void hideNoFilter(Object o) {
		hiddenNoFilter.addAll(entryWrapper.entryList(o));
	}

	public void hideAll(@Nullable Object except) {
		hide(IngredientHelper.get().wildcard(), except);
	}

	public void hideAll() {
		hideAll(null);
	}

	@Override
	protected void afterPosted(EventResult result) {
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