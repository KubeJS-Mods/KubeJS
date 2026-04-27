package dev.latvian.mods.kubejs.server.tag;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.script.ScriptType;
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
			if (ScriptType.SERVER.console.shouldPrintDebug()) {
				ScriptType.SERVER.console.debug("+ %s // %s".formatted(this, filter));
			}
		} else if (DevProperties.get().logSkippedTags) {
			ScriptType.SERVER.console.warn("+ %s // %s [No matches found!]".formatted(this, filter));
		}

		return this;
	}

	public TagWrapper remove(Object... filters) {
		var filter = TagEventFilter.unwrap(event, filters);

		if (filter.remove(this)) {
			if (ScriptType.SERVER.console.shouldPrintDebug()) {
				ScriptType.SERVER.console.debug("- %s // %s".formatted(this, filter));
			}
		} else if (DevProperties.get().logSkippedTags) {
			ScriptType.SERVER.console.warn("- %s // %s [No matches found!]".formatted(this, filter));
		}

		return this;
	}

	public TagWrapper removeAll() {
		ScriptType.SERVER.console.info("TagWrapper.removeAll has been renamed to replace to better represent what the method does!");
		return replace();
	}

	public TagWrapper replace() {
		if (ScriptType.SERVER.console.shouldPrintDebug()) {
			ScriptType.SERVER.console.debug("- %s // (all)".formatted(this));
		}

		if (!builder().isEmpty()) {
			builder().replace();
		} else if (DevProperties.get().logSkippedTags) {
			ScriptType.SERVER.console.warn("- %s // (all) [No matches found!]".formatted(this));
		}

		return this;
	}
}
