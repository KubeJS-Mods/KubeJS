package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.EntryType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public record EntryWrapper<T, C>(EntryType<T> type, TypeInfo typeInfo, Function<Object, C> cast, Function<C, ? extends Predicate<T>> filter, Function<C, ? extends Iterable<T>> entries) {
	public C cast(Context cx, Object from) {
		return cast.apply(cx.jsToJava(from, typeInfo));
	}

	public List<EntryStack<T>> entryList(Context cx, Object from) {
		var list = new ArrayList<EntryStack<T>>();

		for (var in : ListJS.orSelf(from)) {
			for (var entry : entries.apply(cast(cx, in))) {
				list.add(EntryStack.of(type, entry));
			}
		}

		return list;
	}

	public Predicate<T> filter(Context cx, Object from) {
		return filter.apply(cast(cx, from));
	}
}
