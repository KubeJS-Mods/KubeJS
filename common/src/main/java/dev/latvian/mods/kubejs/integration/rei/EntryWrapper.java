package dev.latvian.mods.kubejs.integration.rei;

import me.shedaniel.rei.api.common.entry.EntryStack;

import java.util.Collection;

public interface EntryWrapper {
	Collection<EntryStack<?>> wrap(Object o);
}
