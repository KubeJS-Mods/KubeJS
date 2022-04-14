package dev.latvian.mods.kubejs.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import dev.latvian.mods.kubejs.core.TagBuilderKJS;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class TagEventJS<T> extends EventJS {
	private static String getIdOfEntry(Tag.Entry entry) {
		var asJson = Util.make(new JsonArray(), entry::serializeTo).get(0);
		if (asJson instanceof JsonObject obj) {
			return obj.get("id").getAsString();
		} else {
			return asJson.getAsString();
		}
	}

	@Nullable
	private static List<Predicate<String>> parsePriorityList(@Nullable Object o) {
		if (o == null) {
			return null;
		}

		List<Predicate<String>> list = new ArrayList<>();

		for (var o1 : ListJS.orSelf(o)) {
			var s = String.valueOf(o1);

			if (s.startsWith("@")) {
				var m = s.substring(1);
				list.add(id -> id.startsWith(m));
			} else if (s.startsWith("!@")) {
				var m = s.substring(2);
				list.add(id -> !id.startsWith(m));
			} else {
				list.add(id -> id.equals(s));
			}
		}

		return list.isEmpty() ? null : list;
	}

	public static class TagWrapper<T> {
		private final TagEventJS<T> event;
		private final ResourceLocation id;
		private final Tag.Builder builder;
		private final List<Tag.BuilderEntry> proxyList;
		private List<Predicate<String>> priorityList;

		private TagWrapper(TagEventJS<T> e, ResourceLocation i, Tag.Builder t) {
			event = e;
			id = i;
			builder = t;
			proxyList = ((TagBuilderKJS) builder).getProxyListKJS();
			priorityList = null;
		}

		@Override
		public String toString() {
			return "<%s / %s>".formatted(event.type, id);
		}

		public TagWrapper<T> add(String... ids) {
			for (var stringId : ids) {
				gatherTargets(stringId).ifLeft(wrapper -> {
					builder.addTag(wrapper.id, KubeJS.MOD_ID);
					event.addedCount += wrapper.proxyList.size();

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

					event.addedCount += matches.size();
					for (var holder : matches) {
						var id = holder.key().location();
						builder.addElement(id, KubeJS.MOD_ID);

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

		public TagWrapper<T> remove(String... ids) {
			for (var stringId : ids) {
				gatherTargets(stringId).ifLeft(wrapper -> {
					var entryId = wrapper.id.toString();
					var originalSize = proxyList.size();
					proxyList.removeIf(proxy -> getIdOfEntry(proxy.entry()).equals(entryId));

					var removedCount = proxyList.size() - originalSize;
					if (removedCount == 0) {
						if (ServerSettings.instance.logSkippedRecipes) {
							ConsoleJS.SERVER.warn("- %s // #%s [No matches found!]".formatted(this, entryId));
						}
					} else {
						event.removedCount -= removedCount;

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
							if (getIdOfEntry(proxy.entry()).equals(id.toString())) {
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

					var removedCount = proxyList.size() - originalSize;
					if (removedCount == 0) {
						if (ServerSettings.instance.logSkippedRecipes) {
							ConsoleJS.SERVER.warn("- %s // %s [No matches found!]".formatted(this, stringId));
						}
					} else {
						event.removedCount -= removedCount;
					}
				});
			}

			return this;
		}

		public TagWrapper<T> removeAll() {
			if (ConsoleJS.SERVER.shouldPrintDebug()) {
				ConsoleJS.SERVER.debug("- " + this + " // (all)");
			}

			if (!proxyList.isEmpty()) {
				event.removedCount += proxyList.size();
				proxyList.clear();
			} else if (ServerSettings.instance.logSkippedRecipes) {
				ConsoleJS.SERVER.warn("Tag " + this + " didn't contain any elements, skipped");
			}

			return this;
		}

		public void setPriorityList(@Nullable Object o) {
			priorityList = parsePriorityList(o);
		}

		private void gatherAllItemIDs(HashSet<String> set, Tag.Entry entry) {
			// FIXME: use AW? or maybe we could do something better
			if (entry instanceof Tag.ElementEntry) {
				set.add(entry.toString());
			} else if (entry instanceof Tag.TagEntry) {
				var w = event.tags.get(new ResourceLocation(entry.toString().substring(1)));

				if (w != null && w != this) {
					for (var proxy : w.proxyList) {
						gatherAllItemIDs(set, proxy.entry());
					}
				}
			}
		}

		private Either<TagWrapper<T>, List<Holder.Reference<T>>> gatherTargets(String target) {
			if (target.isEmpty()) {
				return Either.right(List.of());
			}
			var suffix = target.substring(1);
			return switch (target.charAt(0)) {
				case '#' -> Either.left(event.get(new ResourceLocation(suffix)));
				case '@' -> Either.right(ofKeySet(id -> id.location().getNamespace().equals(suffix)));
				case '/' -> Either.right(ofKeySet(id -> UtilsJS.parseRegex(target).matcher(id.location().toString()).find()));
				default -> {
					var id = ResourceKey.create(event.registry.key(), new ResourceLocation(target));
					yield UtilsJS.cast(Either.right(List.of(event.registry.getHolderOrThrow(id))));
				}
			};
		}

		private List<Holder.Reference<T>> ofKeySet(Predicate<ResourceKey<T>> predicate) {
			return event.registry.holders().filter(ref -> ref.is(predicate)).toList();
		}

		/**
		 * Yes its not as efficient as it could be, but this doesn't get run too often. This way it keeps order of original tags.
		 */
		public boolean sort() {
			List<List<Tag.BuilderEntry>> listOfLists = new ArrayList<>();

			for (var i = 0; i < priorityList.size() + 1; i++) {
				listOfLists.add(new ArrayList<>());
			}

			for (var proxy : proxyList) {
				var added = false;

				var set = new HashSet<String>();
				gatherAllItemIDs(set, proxy.entry());

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

			List<Tag.BuilderEntry> proxyList0 = new ArrayList<>(proxyList);

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
	private final Map<ResourceLocation, Tag.Builder> map;
	private final Registry<T> registry;
	private Map<ResourceLocation, TagWrapper<T>> tags;
	private int addedCount;
	private int removedCount;
	private List<Predicate<String>> globalPriorityList;

	public TagEventJS(String t, Map<ResourceLocation, Tag.Builder> m, Registry<T> r) {
		type = t;
		map = m;
		registry = r;
		addedCount = 0;
		removedCount = 0;
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
					tagBuilder.getEntries().forEach(builderEntry -> lines.add("- " + builderEntry.entry()));
				});

				lines.add(0, "To refresh this file, delete it and run /reload command again! Last updated: " + DateFormat.getDateTimeInstance().format(new Date()));

				Files.write(dumpFile, lines);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}

		tags = new HashMap<>();

		for (var entry : map.entrySet()) {
			var w = new TagWrapper<>(this, entry.getKey(), entry.getValue());
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
				entry.getValue().getEntries().forEach(e -> a.add(e.entry().toString()));
				tj1.add(entry.getKey().toString(), a);
			}
		}

		if (addedCount > 0 || removedCount > 0 || ConsoleJS.SERVER.shouldPrintDebug()) {
			ConsoleJS.SERVER.info("[" + type + "] Found " + tags.size() + " tags, added " + addedCount + " objects, removed " + removedCount + " objects"/*, reordered " + reordered + " tags"*/);
		}
	}

	public TagWrapper<T> get(ResourceLocation id) {
		var t = tags.get(id);

		if (t == null) {
			t = new TagWrapper<>(this, id, Tag.Builder.tag());
			tags.put(id, t);
			map.put(id, t.builder);
		}

		return t;
	}

	public TagWrapper<T> add(ResourceLocation tag, String... ids) {
		return get(tag).add(ids);
	}

	public TagWrapper<T> remove(ResourceLocation tag, String... ids) {
		return get(tag).remove(ids);
	}

	public TagWrapper<T> removeAll(ResourceLocation tag) {
		return get(tag).removeAll();
	}

	public void removeAllTagsFrom(String... ids) {
		for (var id : ids) {
			for (var tagWrapper : tags.values()) {
				tagWrapper.proxyList.removeIf(proxy -> getIdOfEntry(proxy.entry()).equals(id));
			}
		}
	}

	public void setGlobalPriorityList(@Nullable Object o) {
		globalPriorityList = parsePriorityList(o);
	}
}