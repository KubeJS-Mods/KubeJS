package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import me.shedaniel.rei.api.client.entry.filtering.base.BasicFilteringRule;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.EntryType;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Stream;

/**
 * @author shedaniel
 * <p>
 * TODO: Rework in 1.19, this is basically just a straight port to the new API
 *   (sorry future max, i don't have time, you're gonna have to rework this even later, maybe 1.19.3)
 */
public class HideREIEventJS<T> extends EventJS {
	private final EntryRegistry registry;
	private final BasicFilteringRule<?> rule;
	private final EntryType<T> type;
	private final EntryWrapper entryWrapper;
	private final Collection<EntryStack<T>> hidden = new HashSet<>();
	private final Collection<EntryStack<T>> hiddenNoFilter = new HashSet<>();
	private boolean hideAll = false;

	public HideREIEventJS(EntryRegistry registry, BasicFilteringRule<?> rule, EntryType<T> type, EntryWrapper entryWrapper) {
		this.registry = registry;
		this.rule = rule;
		this.type = type;
		this.entryWrapper = entryWrapper;
	}

	private Stream<EntryStack<T>> allEntries() {
		return UtilsJS.cast(registry.getEntryStacks().filter(this::filterType));
	}

	public Collection<T> getAllIngredients() {
		return allEntries().map(EntryStack::getValue).toList();
	}

	private boolean filterType(EntryStack<?> stack) {
		return stack.getType().equals(type);
	}

	public void hide(Object o) {
		if (!hideAll) {
			for (var stack : entryWrapper.wrap(o)) {
				hidden.add(stack.cast());
			}
		}
	}

	public void hideNoFilter(Object o) {
		if (!hideAll) {
			for (var stack : entryWrapper.wrap(o)) {
				hiddenNoFilter.add(stack.cast());
			}
		}
	}

	public void hideAll() {
		hideAll = true;
	}

	@Override
	protected void afterPosted(boolean result) {
		if (hideAll) {
			rule.hide(allEntries().toList());
		}
		if (!hidden.isEmpty()) {
			rule.hide(hidden);
		}

		hiddenNoFilter.forEach(registry::removeEntry);
	}
}