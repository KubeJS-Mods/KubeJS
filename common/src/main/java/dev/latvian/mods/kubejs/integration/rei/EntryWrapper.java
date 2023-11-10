package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.util.ListJS;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.EntryType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public record EntryWrapper<T, C>(EntryType<T> type, Function<Object, C> converter, Function<C, ? extends Predicate<T>> filter, Function<C, ? extends Iterable<T>> entries) {
	public List<EntryStack<T>> entryList(Object input) {
		var list = new ArrayList<EntryStack<T>>();

		for (var in : ListJS.orSelf(input)) {
			for (var entry : entries.apply(converter.apply(in))) {
				list.add(EntryStack.of(type, entry));
			}
		}

		return list;
	}

	public Predicate<T> filter(Object input) {
		return filter.apply(converter.apply(input));
	}
}
