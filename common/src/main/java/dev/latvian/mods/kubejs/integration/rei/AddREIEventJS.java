package dev.latvian.mods.kubejs.integration.rei;

import com.google.common.collect.Lists;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.util.ListJS;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;

import java.util.List;

public class AddREIEventJS extends EventJS {
	private final EntryRegistry registry;
	private final EntryWrapper entryWrapper;
	private final List<EntryStack<?>> added = Lists.newArrayList();

	public AddREIEventJS(EntryRegistry registry, EntryWrapper entryWrapper) {
		this.registry = registry;
		this.entryWrapper = entryWrapper;
	}

	public void add(Object o) {
		for (var o1 : ListJS.orSelf(o)) {
			var stacks = entryWrapper.wrap(o1);

			if (stacks != null && !stacks.isEmpty()) {
				for (var stack : stacks) {
					if (stack != null && !stack.isEmpty()) {
						added.add(stack);
					}
				}
			}
		}
	}

	@Override
	protected void afterPosted(EventResult result) {
		if (!added.isEmpty()) {
			registry.addEntries(added);
		}
	}
}