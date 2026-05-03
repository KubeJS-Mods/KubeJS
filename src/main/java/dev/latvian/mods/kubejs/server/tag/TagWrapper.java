package dev.latvian.mods.kubejs.server.tag;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import net.minecraft.resources.Identifier;

public record TagWrapper(TagKubeEvent event, Identifier id) {
	TagEventBuilder builder() {
		return event.getOrCreateBuilder(id);
	}

	@Override
	public String toString() {
		return "<%s / #%s>".formatted(event.getType(), id);
	}

	public TagWrapper add(Object... filters) {
		var filter = TagEventFilter.unwrap(event, filters);

		if (filter.add(this)) {
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

		if (filter.remove(this)) {
			if (ConsoleJS.SERVER.shouldPrintDebug()) {
				ConsoleJS.SERVER.debug("- %s // %s".formatted(this, filter));
			}
		} else if (DevProperties.get().logSkippedTags) {
			ConsoleJS.SERVER.warn("- %s // %s [No matches found!]".formatted(this, filter));
		}

		return this;
	}

	public TagWrapper removeAll() {
		ConsoleJS.SERVER.info("TagWrapper.removeAll has been renamed to replace to better represent what the method does!");
		return replace();
	}

	public TagWrapper replace() {
		if (ConsoleJS.SERVER.shouldPrintDebug()) {
			ConsoleJS.SERVER.debug("- %s // (all)".formatted(this));
		}

		if (!builder().isEmpty()) {
			builder().replace();
		} else if (DevProperties.get().logSkippedTags) {
			ConsoleJS.SERVER.warn("- %s // (all) [No matches found!]".formatted(this));
		}

		return this;
	}
}
