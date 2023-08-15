package dev.latvian.mods.kubejs.server;

import com.google.gson.JsonArray;
import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagLoader;

import java.nio.file.Files;
import java.text.DateFormat;
import java.util.*;
import java.util.function.Predicate;

public class TagEventJS<T> extends EventJS {
	private static final EventHandler.EventExceptionHandler TAG_EVENT_HANDLER = (event, container, ex) -> {
		if (ex instanceof IllegalStateException) {
			var error = ex.getCause() == null ? ex : ex.getCause();
			ConsoleJS.SERVER.handleError(error, null, "IllegalStateException was thrown during tag event in script %s:%d, this is most likely due to a concurrency bug in Rhino!"
					.formatted(container.source, container.line));
			ConsoleJS.SERVER.error("While we are working on a fix for this issue, you may manually work around it by reloading the server again (e.g. by using /reload command).");
			return null;
		}
		return ex;
	};

	public class TagWrapper {
		private final ResourceLocation id;
		private final List<TagLoader.EntryWithSource> entries;

		private TagWrapper(ResourceLocation i, List<TagLoader.EntryWithSource> t) {
			id = i;
			entries = t;
		}

		@Override
		public String toString() {
			return "<%s / %s>".formatted(getType(), id);
		}

		public TagWrapper add(String... ids) {
			for (var stringId : ids) {
				gatherTargets(stringId).ifLeft(wrapper -> {
					entries.add(new TagLoader.EntryWithSource(TagEntry.tag(wrapper.id), "KubeJS Custom Tags"));
					totalAdded += wrapper.entries.size();

					if (ConsoleJS.SERVER.shouldPrintDebug()) {
						ConsoleJS.SERVER.debug("+ %s // #%s".formatted(this, wrapper.id));
					}
				}).ifRight(matches -> {
					if (matches.isEmpty()) {
						if (DevProperties.get().logSkippedTags) {
							ConsoleJS.SERVER.warn("+ %s // %s [No matches found!]".formatted(this, stringId));
						}
						return;
					}

					totalAdded += matches.size();
					for (var holder : matches) {
						var id = holder.key().location();
						entries.add(new TagLoader.EntryWithSource(TagEntry.element(id), "KubeJS Custom Tags"));

						if (ConsoleJS.SERVER.shouldPrintDebug()) {
							if (id.toString().equals(stringId)) {
								ConsoleJS.SERVER.debug("+ %s // %s".formatted(this, id));
							} else {
								ConsoleJS.SERVER.debug("+ %s // %s (via %s)".formatted(this, id, stringId));
							}
						}
					}
				});
			}

			return this;
		}

		public TagWrapper remove(String... ids) {
			for (var stringId : ids) {
				gatherTargets(stringId).ifLeft(wrapper -> {
					var entryId = wrapper.id;
					var originalSize = entries.size();
					entries.removeIf(proxy -> {
						var proxyEntry = proxy.entry();
						return proxyEntry.tag && proxyEntry.id.equals(entryId);
					});

					var removedCount = originalSize - entries.size();
					if (removedCount == 0) {
						if (DevProperties.get().logSkippedTags) {
							ConsoleJS.SERVER.warn("- %s // #%s [No matches found!]".formatted(this, entryId));
						}
					} else {
						totalRemoved += removedCount;

						if (ConsoleJS.SERVER.shouldPrintDebug()) {
							ConsoleJS.SERVER.debug("- %s // %s".formatted(this, entryId));
						}
					}
				}).ifRight(matches -> {
					var originalSize = entries.size();

					for (var holder : matches) {
						var id = holder.key().location();
						for (var iterator = entries.listIterator(); iterator.hasNext(); ) {
							var proxy = iterator.next();
							if (proxy.entry().elementOrTag().decoratedId().equals(id.toString())) {
								iterator.remove();
								if (ConsoleJS.SERVER.shouldPrintDebug()) {
									if (id.toString().equals(stringId)) {
										ConsoleJS.SERVER.debug("- %s // %s".formatted(this, id));
									} else {
										ConsoleJS.SERVER.debug("- %s // %s (via %s)".formatted(this, id, stringId));
									}
								}
								break;
							}
						}
					}

					var removedCount = originalSize - entries.size();
					if (removedCount == 0) {
						if (DevProperties.get().logSkippedTags) {
							ConsoleJS.SERVER.warn("- %s // %s [No matches found!]".formatted(this, stringId));
						}
					} else {
						totalRemoved += removedCount;
					}
				});
			}

			return this;
		}

		public TagWrapper removeAll() {
			if (ConsoleJS.SERVER.shouldPrintDebug()) {
				ConsoleJS.SERVER.debug("- %s // (all)".formatted(this));
			}

			if (!entries.isEmpty()) {
				totalRemoved += entries.size();
				entries.clear();
			} else if (DevProperties.get().logSkippedTags) {
				ConsoleJS.SERVER.warn("Tag " + this + " didn't contain any elements, skipped");
			}

			return this;
		}

		public Collection<ResourceLocation> getObjectIds() {
			var set = new LinkedHashSet<ResourceLocation>();
			for (var proxy : entries) {
				gatherIdsFor(set, proxy);
			}
			return set;
		}

		private void gatherIdsFor(Collection<ResourceLocation> collection, TagLoader.EntryWithSource entry) {
			var id = entry.entry().elementOrTag();
			if (id.tag()) {
				// tag entry, recurse
				var w = tags.get(id.id());
				if (w != null && w != this) {
					for (var proxy : w.entries) {
						gatherIdsFor(collection, proxy);
					}
				}
			} else {
				// verify that the entry is actually contained in the registry
				var entryId = id.id();
				if (registry.containsKey(entryId)) {
					collection.add(entryId);
				}
			}
		}
	}

	public final String directory;
	private final Map<ResourceLocation, List<TagLoader.EntryWithSource>> map;
	private final Registry<T> registry;
	private Map<ResourceLocation, TagWrapper> tags;
	private int totalAdded;
	private int totalRemoved;

	public TagEventJS(String dir, Map<ResourceLocation, List<TagLoader.EntryWithSource>> m, Registry<T> r) {
		directory = dir;
		map = m;
		registry = r;
		totalAdded = 0;
		totalRemoved = 0;
	}

	public ResourceLocation getType() {
		return registry.key().location();
	}

	public void post() {
		var dumpFile = KubeJSPaths.EXPORT.resolve("tags/" + getType().getNamespace() + "/" + getType().getPath() + ".txt");

		if (!Files.exists(dumpFile)) {
			try {
				if (!Files.exists(dumpFile.getParent())) {
					Files.createDirectories(dumpFile.getParent());
				}

				List<String> lines = new ArrayList<>();

				map.forEach((tagId, entries) -> {
					lines.add("");
					lines.add("#" + tagId);
					entries.forEach(entry -> lines.add("- " + entry));
				});

				lines.add(0, "To refresh this file, delete it and run /reload command again! Last updated: " + DateFormat.getDateTimeInstance().format(new Date()));

				Files.write(dumpFile, lines);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}

		tags = new HashMap<>();

		for (var entry : map.entrySet()) {
			var w = new TagWrapper(entry.getKey(), entry.getValue());
			tags.put(entry.getKey(), w);
			ConsoleJS.SERVER.debug("%s/#%s; %d".formatted(getType(), entry.getKey(), w.entries.size()));
		}

		var types = RegistryInfo.MAP.get(registry.key());

		if (types != null) {
			for (var builder : types.objects.values()) {
				for (var s : builder.defaultTags) {
					add(s, builder.id.toString());
				}
			}
		}

		ServerEvents.TAGS.post(this, registry.key(), TAG_EVENT_HANDLER);

		if (DataExport.export != null) {
			var loc = "tags/" + getType().toString() + "/";

			for (var entry : map.entrySet()) {
				var list = new ArrayList<String>();

				for (var e : entry.getValue()) {
					list.add(e.entry().toString());
				}

				list.sort(String.CASE_INSENSITIVE_ORDER);
				var arr = new JsonArray();

				for (var e : list) {
					arr.add(e);
				}

				DataExport.export.addJson(loc + entry.getKey() + ".json", arr);
			}
		}

		if (totalAdded > 0 || totalRemoved > 0 || ConsoleJS.SERVER.shouldPrintDebug()) {
			ConsoleJS.SERVER.info("[%s] Found %d tags, added %d objects, removed %d objects".formatted(getType(), tags.size(), totalAdded, totalRemoved));
		}
	}

	public TagWrapper get(ResourceLocation id) {
		var t = tags.get(id);

		if (t == null) {
			t = new TagWrapper(id, new ArrayList<>());
			tags.put(id, t);
			map.put(id, t.entries);
		}

		return t;
	}

	public TagWrapper add(ResourceLocation tag, String... ids) {
		return get(tag).add(ids);
	}

	public TagWrapper remove(ResourceLocation tag, String... ids) {
		return get(tag).remove(ids);
	}

	public TagWrapper removeAll(ResourceLocation tag) {
		return get(tag).removeAll();
	}

	public void removeAllTagsFrom(String... ids) {
		for (var id : ids) {
			for (var tagWrapper : tags.values()) {
				tagWrapper.entries.removeIf(proxy -> proxy.entry().elementOrTag().decoratedId().equals(id));
			}
		}
	}

	private Either<TagWrapper, List<Holder.Reference<T>>> gatherTargets(String target) {
		if (target.isEmpty()) {
			return Either.right(List.of());
		}
		var suffix = target.substring(1);
		return switch (target.charAt(0)) {
			case '#' -> Either.left(get(new ResourceLocation(suffix)));
			case '@' -> Either.right(ofKeySet(id -> id.location().getNamespace().equals(suffix)));
			case '/' -> Either.right(ofKeySet(id -> UtilsJS.parseRegex(target).matcher(id.location().toString()).find()));
			default -> {
				var id = ResourceKey.create(registry.key(), new ResourceLocation(target));
				yield UtilsJS.cast(Either.right(List.of(registry.getHolderOrThrow(id))));
			}
		};
	}

	private List<Holder.Reference<T>> ofKeySet(Predicate<ResourceKey<T>> predicate) {
		return registry.holders().filter(ref -> ref.is(predicate)).toList();
	}
}