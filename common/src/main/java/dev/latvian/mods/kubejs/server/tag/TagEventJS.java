package dev.latvian.mods.kubejs.server.tag;

import dev.latvian.mods.kubejs.event.EventExceptionHandler;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TagEventJS extends EventJS {
	public static final EventExceptionHandler TAG_EVENT_HANDLER = (event, container, ex) -> {
		if (ex instanceof IllegalStateException) {
			var stacktrace = ex.getStackTrace();
			if (stacktrace.length > 0) {
				// if the first element in the stack trace is dev.latvian.mods.rhino.ScriptRuntime.doTopCall, then it's definitely the concurrency bug
				// (todo: if this is too specific of an assumption and there are other cases where rhino throws a concurrency related IllegalStateException,
				//   this needs to be turned into a more general check again, but i kinda didn't want to go through the entire stacktrace here)
				if (stacktrace[0].toString().contains("dev.latvian.mods.rhino.ScriptRuntime.doTopCall")) {
					var error = ex.getCause() == null ? ex : ex.getCause();
					ConsoleJS.SERVER.handleError(error, null, "IllegalStateException was thrown during tag event in script %s:%d, this is most likely due to a concurrency bug in Rhino!"
						.formatted(container.source, container.line));
					ConsoleJS.SERVER.error("While we are working on a fix for this issue, you may manually work around it by reloading the server again (e.g. by using /reload command).");
					return null;
				}
			}
		} else if (ex instanceof EmptyTagTargetException) {
			ConsoleJS.SERVER.error(ex.getMessage() + " (at %s:%d)".formatted(container.source, container.line));
			return null;
		}
		return ex;
	};

	public static final String SOURCE = "KubeJS Custom Tags";

	public final RegistryInfo registry;
	public final Map<ResourceLocation, TagWrapper> tags;
	public int totalAdded;
	public int totalRemoved;
	private Set<ResourceLocation> elementIds;

	public TagEventJS(RegistryInfo ri) {
		registry = ri;
		tags = new ConcurrentHashMap<>();
		totalAdded = 0;
		totalRemoved = 0;
	}

	public ResourceLocation getType() {
		return registry.key.location();
	}

	public TagWrapper get(ResourceLocation id) {
		return tags.computeIfAbsent(id, this::createTagWrapper);
	}

	protected TagWrapper createTagWrapper(ResourceLocation id) {
		return new TagWrapper(this, id, new ArrayList<>());
	}

	public TagWrapper add(ResourceLocation tag, Object... filters) {
		return get(tag).add(filters);
	}

	public TagWrapper remove(ResourceLocation tag, Object... filters) {
		return get(tag).remove(filters);
	}

	public TagWrapper removeAll(ResourceLocation tag) {
		return get(tag).removeAll();
	}

	public void removeAllTagsFrom(Object... ids) {
		var filter = TagEventFilter.unwrap(this, ids);

		for (var tagWrapper : tags.values()) {
			tagWrapper.entries.removeIf(proxy -> filter.testTagOrElementLocation(proxy.entry().elementOrTag()));
		}
	}

	public Set<ResourceLocation> getElementIds() {
		if (elementIds == null) {
			elementIds = UtilsJS.cast(registry.getVanillaRegistry().holders().map(Holder.Reference::key).map(ResourceKey::location).collect(Collectors.toSet()));
		}

		return elementIds;
	}

	void gatherIdsFor(TagWrapper excluded, Collection<ResourceLocation> collection, TagLoader.EntryWithSource entry) {
		var id = entry.entry().elementOrTag();

		if (id.tag()) {
			// tag entry, recurse
			var w = tags.get(id.id());
			if (w != null && w != excluded) {
				for (var proxy : w.entries) {
					gatherIdsFor(excluded, collection, proxy);
				}
			}
		} else {
			// verify that the entry is actually contained in the registry
			var entryId = id.id();

			if (getElementIds().contains(entryId)) {
				collection.add(entryId);
			}
		}
	}
}