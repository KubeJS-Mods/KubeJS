package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import me.shedaniel.rei.api.common.util.EntryStacks;

import java.util.Collection;

/**
 * @author shedaniel
 */
public class HideREIEventJS<T> extends EventJS {
	private final EntryRegistry registry;
	private final EntryType<T> type;
	private final EntryWrapper entryWrapper;
	private final LongSet hidden = new LongOpenHashSet();
	private boolean hideAll = false;

	public HideREIEventJS(EntryRegistry registry, EntryType<T> type, EntryWrapper entryWrapper) {
		this.registry = registry;
		this.type = type;
		this.entryWrapper = entryWrapper;
	}

	public Collection<T> getAllIngredients() {
		return UtilsJS.cast(registry.getEntryStacks().filter(this::filterType).map(EntryStack::getValue).toList());
	}

	private boolean filterType(EntryStack<?> stack) {
		return stack.getType().equals(type);
	}

	public void hide(Object o) {
		if (!hideAll) {
			for (var stack : entryWrapper.wrap(o)) {
				hidden.add(EntryStacks.hashExact(stack));
			}
		}
	}

	public void hideAll() {
		hideAll = true;
	}

	@Override
	protected void afterPosted(boolean result) {
		if (!hidden.isEmpty()) {
			registry.removeEntryIf(stack -> {
				if (filterType(stack)) {
					return hideAll || hidden.contains(EntryStacks.hashExact(stack));
				}

				return false;
			});
		}
	}
}