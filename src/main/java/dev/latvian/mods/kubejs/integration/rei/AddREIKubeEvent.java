package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.event.KubeEvent;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;

import java.util.ArrayList;
import java.util.List;

public class AddREIKubeEvent<T, C> implements KubeEvent {
	private final EntryRegistry registry;
	private final EntryWrapper<T, C> entryWrapper;
	private final List<EntryStack<T>> added;

	public AddREIKubeEvent(EntryRegistry registry, EntryWrapper<T, C> entryWrapper) {
		this.registry = registry;
		this.entryWrapper = entryWrapper;
		this.added = new ArrayList<>();
	}

	public void add(Object o) {
		added.addAll(entryWrapper.entryList(o));
	}

	@Override
	public void afterPosted(EventResult result) {
		if (!added.isEmpty()) {
			registry.addEntries(added);
		}
	}
}