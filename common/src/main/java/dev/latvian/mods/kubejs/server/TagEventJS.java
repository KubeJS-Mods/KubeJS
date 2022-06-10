package dev.latvian.mods.kubejs.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class TagEventJS<T> extends EventJS {
	@Nullable
	private static List<Predicate<ResourceLocation>> parsePriorityList(@Nullable Object o) {
		if (o == null) {
			return null;
		}

		List<Predicate<ResourceLocation>> list = new ArrayList<>();

		for (var o1 : ListJS.orSelf(o)) {
			var s = String.valueOf(o1);

			if (s.startsWith("@")) {
				var m = s.substring(1);
				list.add(id -> id.getNamespace().equals(m));
			} else if (s.startsWith("!@")) {
				var m = s.substring(2);
				list.add(id -> !id.getNamespace().equals(m));
			} else {
				list.add(id -> id.equals(UtilsJS.getMCID(s)));
			}
		}

		return list.isEmpty() ? null : list;
	}

	public class TagWrapper {
		private final ResourceLocation id;
		private final TagBuilder builder;
		private final List<TagEntry> proxyList;
		private List<Predicate<ResourceLocation>> priorityList;

		private TagWrapper(ResourceLocation i, TagBuilder t) {
			id = i;
			builder = t;
			proxyList = builder.entries;
			priorityList = null;
		}

		@Override
		public String toString() {
			return "<%s / %s>".formatted(type, id);
		}

		public TagWrapper add(String... ids) {
			for (var stringId : ids) {
				gatherTargets(stringId).ifLeft(wrapper -> {
					builder.addTag(wrapper.id);
					totalAdded += wrapper.proxyList.size();

					if (ConsoleJS.SERVER.shouldPrintDebug()) {
						ConsoleJS.SERVER.debug("+ %s // #%s".formatted(this, wrapper.id));
					}
				}).ifRight(matches -> {
					if (matches.isEmpty()) {
						if (ServerSettings.instance.logSkippedRecipes) {
							ConsoleJS.SERVER.warn("+ %s // %s [No matches found!]".formatted(this, stringId));
						}
						return;
					}

					totalAdded += matches.size();
					for (var holder : matches) {
						var id = holder.key().location();
						builder.addElement(id);

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
					var entryId = wrapper.id.toString();
					var originalSize = proxyList.size();
					proxyList.removeIf(proxy -> proxy.elementOrTag().decoratedId().equals(entryId));

					var removedCount = originalSize - proxyList.size();
					if (removedCount == 0) {
						if (ServerSettings.instance.logSkippedRecipes) {
							ConsoleJS.SERVER.warn("- %s // #%s [No matches found!]".formatted(this, entryId));
						}
					} else {
						totalRemoved += removedCount;

						if (ConsoleJS.SERVER.shouldPrintDebug()) {
							ConsoleJS.SERVER.debug("- " + this + " // " + entryId);
						}
					}
				}).ifRight(matches -> {
					var originalSize = proxyList.size();

					for (var holder : matches) {
						var id = holder.key().location();
						for (var iterator = proxyList.listIterator(); iterator.hasNext(); ) {
							var proxy = iterator.next();
							if (proxy.elementOrTag().decoratedId().equals(id.toString())) {
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

					var removedCount = originalSize - proxyList.size();
					if (removedCount == 0) {
						if (ServerSettings.instance.logSkippedRecipes) {
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
				ConsoleJS.SERVER.debug("- " + this + " // (all)");
			}

			if (!proxyList.isEmpty()) {
				totalRemoved += proxyList.size();
				proxyList.clear();
			} else if (ServerSettings.instance.logSkippedRecipes) {
				ConsoleJS.SERVER.warn("Tag " + this + " didn't contain any elements, skipped");
			}

			return this;
		}

		public Collection<ResourceLocation> getObjectIds() {
			var set = new LinkedHashSet<ResourceLocation>();
			for (var proxy : proxyList) {
				gatherIdsFor(set, proxy);
			}
			return set;
		}

		private void gatherIdsFor(Collection<ResourceLocation> collection, TagEntry entry) {
			var id = entry.elementOrTag();
			if (id.tag()) {
				// tag entry, recurse
				var w = tags.get(id.id());
				if (w != null && w != this) {
					for (var proxy : w.proxyList) {
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

		public void setPriorityList(@Nullable Object o) {
			priorityList = parsePriorityList(o);
		}

		/**
		 * Yes its not as efficient as it could be, but this doesn't get run too often. This way it keeps order of original tags.
		 */
		public boolean sort() {
			List<List<TagEntry>> listOfLists = new ArrayList<>();

			for (var i = 0; i < priorityList.size() + 1; i++) {
				listOfLists.add(new ArrayList<>());
			}

			for (var proxy : proxyList) {
				var added = false;

				var set = new HashSet<ResourceLocation>();
				gatherIdsFor(set, proxy);

				for (var id : set) {
					for (var i = 0; i < priorityList.size(); i++) {
						if (priorityList.get(i).test(id)) {
							listOfLists.get(i).add(proxy);
							added = true;
							break;
						}
					}
				}

				if (!added) {
					listOfLists.get(priorityList.size()).add(proxy);
				}
			}

			List<TagEntry> proxyList0 = new ArrayList<>(proxyList);

			proxyList.clear();

			for (var list : listOfLists) {
				proxyList.addAll(list);
			}

			if (!proxyList0.equals(proxyList)) {
				if (ConsoleJS.SERVER.shouldPrintDebug()) {
					ConsoleJS.SERVER.debug("* Re-arranged " + this);
				}

				return true;
			}

			return false;
		}
	}

	private final String type;
	private final Map<ResourceLocation, TagBuilder> map;
	private final Registry<T> registry;
	private Map<ResourceLocation, TagWrapper> tags;
	private int totalAdded;
	private int totalRemoved;
	private List<Predicate<ResourceLocation>> globalPriorityList;

	public TagEventJS(String t, Map<ResourceLocation, TagBuilder> m, Registry<T> r) {
		type = t;
		map = m;
		registry = r;
		totalAdded = 0;
		totalRemoved = 0;
		globalPriorityList = null;
	}

	public String getType() {
		return type;
	}

	public void post(String event) {
		var dumpFile = KubeJSPaths.EXPORTED.resolve("tags/" + type + ".txt");

		if (!Files.exists(dumpFile)) {
			try {
				if (!Files.exists(dumpFile.getParent())) {
					Files.createDirectories(dumpFile.getParent());
				}

				List<String> lines = new ArrayList<>();

				map.forEach((tagId, tagBuilder) -> {
					lines.add("");
					lines.add("#" + tagId);
					tagBuilder.entries.forEach(builderEntry -> lines.add("- " + builderEntry));
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
			ConsoleJS.SERVER.debug("%s/#%s; %d".formatted(type, entry.getKey(), w.proxyList.size()));
		}

		var types = RegistryObjectBuilderTypes.MAP.get(registry.key());

		if (types != null) {
			for (var builder : types.objects.values()) {
				for (var s : builder.defaultTags) {
					add(s, builder.id.toString());
				}
			}
		}

		ConsoleJS.SERVER.setLineNumber(true);
		post(ScriptType.SERVER, event);
		ConsoleJS.SERVER.setLineNumber(false);

		var reordered = 0;

		for (var wrapper : tags.values()) {
			if (wrapper.priorityList == null) {
				wrapper.priorityList = globalPriorityList;
			}

			if (wrapper.priorityList != null && wrapper.sort()) {
				reordered++;
			}
		}

		if (ServerSettings.dataExport != null && registry != null) {
			var tj = ServerSettings.dataExport.getAsJsonObject("tags");

			if (tj == null) {
				tj = new JsonObject();
				ServerSettings.dataExport.add("tags", tj);
			}

			var tj1 = tj.getAsJsonObject(type);

			if (tj1 == null) {
				tj1 = new JsonObject();
				tj.add(type, tj1);
			}

			for (var entry : map.entrySet()) {
				var a = new JsonArray();
				entry.getValue().entries.forEach(e -> a.add(e.toString()));
				tj1.add(entry.getKey().toString(), a);
			}
		}

		if (totalAdded > 0 || totalRemoved > 0 || ConsoleJS.SERVER.shouldPrintDebug()) {
			ConsoleJS.SERVER.info("[" + type + "] Found " + tags.size() + " tags, added " + totalAdded + " objects, removed " + totalRemoved + " objects"/*, reordered " + reordered + " tags"*/);
		}
	}

	public TagWrapper get(ResourceLocation id) {
		var t = tags.get(id);

		if (t == null) {
			t = new TagWrapper(id, new TagBuilder());
			tags.put(id, t);
			map.put(id, t.builder);
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
				tagWrapper.proxyList.removeIf(proxy -> proxy.elementOrTag().decoratedId().equals(id));
			}
		}
	}

	public void setGlobalPriorityList(@Nullable Object o) {
		globalPriorityList = parsePriorityList(o);
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