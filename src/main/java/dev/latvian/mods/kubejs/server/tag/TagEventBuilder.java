package dev.latvian.mods.kubejs.server.tag;

import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagLoader.EntryWithSource;

import java.util.ArrayList;
import java.util.List;

final class TagEventBuilder extends TagBuilder {
	private final List<EntryWithSource> baseEntries;

	TagEventBuilder(List<EntryWithSource> baseEntries) {
		this.baseEntries = List.copyOf(baseEntries);
	}

	boolean isEmpty() {
		return (shouldReplace() || baseEntries.isEmpty()) && entries.isEmpty() && removeEntries.isEmpty();
	}

	List<EntryWithSource> buildEntries() {
		var result = new ArrayList<EntryWithSource>();

		if (!shouldReplace()) {
			result.addAll(baseEntries);
		}

		for (var entry : entries) {
			result.add(new EntryWithSource(entry, TagKubeEvent.SOURCE));
		}

		for (var entry : removeEntries) {
			result.add(new EntryWithSource(entry, TagKubeEvent.SOURCE, true));
		}

		return result;
	}
}
