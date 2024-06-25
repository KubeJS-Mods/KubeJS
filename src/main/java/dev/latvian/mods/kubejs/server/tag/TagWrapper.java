package dev.latvian.mods.kubejs.server.tag;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagLoader;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class TagWrapper {
	public final TagKubeEvent event;
	public final ResourceLocation id;
	public final List<TagLoader.EntryWithSource> entries;

	public TagWrapper(TagKubeEvent e, ResourceLocation i, List<TagLoader.EntryWithSource> t) {
		event = e;
		id = i;
		entries = t;
	}

	@Override
	public String toString() {
		return "<%s / #%s>".formatted(event.getType(), id);
	}

	public TagWrapper add(Object... filters) {
		var filter = TagEventFilter.unwrap(event, filters);
		var addedCount = filter.add(this);

		if (addedCount > 0) {
			event.totalAdded += addedCount;

			if (ConsoleJS.SERVER.shouldPrintDebug()) {
				ConsoleJS.SERVER.debug("+ %s // %s".formatted(this, filter));
			}
		} else if (DevProperties.get().logSkippedTags) {
			ConsoleJS.SERVER.warn("+ %s // %s [No matches found!]".formatted(this, filter));
		}

		return this;
	}

	public TagWrapper remove(Object... filters) {
		var filter = TagEventFilter.unwrap(event, filters);
		var removedCount = filter.remove(this);

		if (removedCount > 0) {
			event.totalRemoved += removedCount;

			if (ConsoleJS.SERVER.shouldPrintDebug()) {
				ConsoleJS.SERVER.debug("- %s // %s".formatted(this, filter));
			}
		} else if (DevProperties.get().logSkippedTags) {
			ConsoleJS.SERVER.warn("- %s // %s [No matches found!]".formatted(this, filter));
		}

		return this;
	}

	public TagWrapper removeAll() {
		if (ConsoleJS.SERVER.shouldPrintDebug()) {
			ConsoleJS.SERVER.debug("- %s // (all)".formatted(this));
		}

		if (!entries.isEmpty()) {
			event.totalRemoved += entries.size();
			entries.clear();
		} else if (DevProperties.get().logSkippedTags) {
			ConsoleJS.SERVER.warn("- %s // (all) [No matches found!]".formatted(this));
		}

		return this;
	}

	public List<ResourceLocation> getObjectIds() {
		var set = new LinkedHashSet<ResourceLocation>();

		for (var proxy : entries) {
			event.gatherIdsFor(this, set, proxy);
		}

		return new ArrayList<>(set);
	}
}