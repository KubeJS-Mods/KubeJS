package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.event.EventResult;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;

import java.util.ArrayList;
import java.util.List;

public class AddREIEventJS<T, C> extends EventJS {
	private final EntryRegistry registry;
	private final EntryWrapper<T, C> entryWrapper;
	private final List<EntryStack<T>> added;

	public AddREIEventJS(EntryRegistry registry, EntryWrapper<T, C> entryWrapper) {
		this.registry = registry;
		this.entryWrapper = entryWrapper;
		this.added = new ArrayList<>();
	}

	public void add(Object o) {
		added.addAll(entryWrapper.entryList(o));
	}

	@Override
	protected void afterPosted(EventResult result) {
		if (!added.isEmpty()) {
			registry.addEntries(added);
		}
	}
}