package dev.latvian.mods.kubejs.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.architectury.registry.registries.Registrar;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSObjects;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.core.TagBuilderKJS;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
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
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class TagEventJS<T> extends EventJS {
	private static String getIdOfEntry(String s) {
		if (s.length() > 0 && s.charAt(s.length() - 1) == '?') {
			return s.substring(0, s.length() - 1);
		}

		return s;
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
			return event.type + ":" + id;
		}

		public TagWrapper<T> add(Object ids) {
			for (var o : ListJS.orSelf(ids)) {
				var s = String.valueOf(o);

				if (s.startsWith("#")) {
					var w = event.get(new ResourceLocation(s.substring(1)));
					builder.addTag(w.id, KubeJS.MOD_ID);
					event.addedCount += w.proxyList.size();

					if (ConsoleJS.SERVER.shouldPrintDebug()) {
						ConsoleJS.SERVER.debug("+ " + this + " // " + w.id);
					}
				} else {
					var pattern = UtilsJS.parseRegex(s);

					if (pattern != null && event.registrar != null) {
						for (var sid : event.registrar.getIds()) {
							if (pattern.matcher(sid.toString()).find()) {
								var v = event.registry.apply(sid);

								if (v.isPresent()) {
									builder.addElement(sid, KubeJS.MOD_ID);
									event.addedCount++;

									if (ConsoleJS.SERVER.shouldPrintDebug()) {
										ConsoleJS.SERVER.debug("+ " + this + " // " + s + " [" + v.get().getClass().getName() + "]");
									}
								} else {
									ConsoleJS.SERVER.error("+ " + this + " // " + s + " [Not found!]");
								}
							}
						}
					} else {
						var sid = new ResourceLocation(s);
						var v = event.registry.apply(sid);

						if (v.isPresent()) {
							builder.addElement(sid, KubeJS.MOD_ID);
							event.addedCount++;

							if (ConsoleJS.SERVER.shouldPrintDebug()) {
								ConsoleJS.SERVER.debug("+ " + this + " // " + s + " [" + v.get().getClass().getName() + "]");
							}
						} else {
							ConsoleJS.SERVER.error("+ " + this + " // " + s + " [Not found!]");
						}
					}
				}
			}

			return this;
		}

		public TagWrapper<T> remove(Object ids) {
			for (var o : ListJS.orSelf(ids)) {
				var s = String.valueOf(o);

				if (s.startsWith("#")) {
					var w = event.get(new ResourceLocation(s.substring(1)));
					var entryId = w.id.toString();
					var originalSize = proxyList.size();
					proxyList.removeIf(proxy -> getIdOfEntry(proxy.entry().toString()).equals(s));
					var removedCount = proxyList.size() - originalSize;

					if (removedCount == 0) {
						if (ServerSettings.instance.logSkippedRecipes) {
							ConsoleJS.SERVER.warn(s + " didn't contain tag " + this + ", skipped");
						}
					} else {
						event.removedCount -= removedCount;

						if (ConsoleJS.SERVER.shouldPrintDebug()) {
							ConsoleJS.SERVER.debug("- " + this + " // " + w.id);
						}
					}
				} else {
					var pattern = UtilsJS.parseRegex(s);

					if (pattern != null && event.registrar != null) {
						for (var sid : event.registrar.getIds()) {
							if (pattern.matcher(sid.toString()).find()) {
								var v = event.registry.apply(sid);

								if (v.isPresent()) {
									var originalSize = proxyList.size();
									proxyList.removeIf(proxy -> getIdOfEntry(proxy.entry().toString()).equals(s));
									var removedCount = proxyList.size() - originalSize;

									if (removedCount == 0) {
										if (ServerSettings.instance.logSkippedRecipes) {
											ConsoleJS.SERVER.warn(s + " didn't contain tag " + id + ", skipped");
										}
									} else {
										event.removedCount -= removedCount;

										if (ConsoleJS.SERVER.shouldPrintDebug()) {
											ConsoleJS.SERVER.debug("- " + this + " // " + s + " [" + v.get().getClass().getName() + "]");
										}
									}
								} else {
									ConsoleJS.SERVER.error("- " + this + " // " + s + " [Not found!]");
								}
							}
						}
					} else {
						var sid = new ResourceLocation(s);
						var v = event.registry.apply(sid);

						if (v.isPresent()) {
							var originalSize = proxyList.size();
							proxyList.removeIf(proxy -> getIdOfEntry(proxy.entry().toString()).equals(s));
							var removedCount = proxyList.size() - originalSize;

							if (removedCount == 0) {
								if (ServerSettings.instance.logSkippedRecipes) {
									ConsoleJS.SERVER.warn(s + " didn't contain tag " + id + ", skipped");
								}
							} else {
								event.removedCount -= removedCount;

								if (ConsoleJS.SERVER.shouldPrintDebug()) {
									ConsoleJS.SERVER.debug("- " + this + " // " + s + " [" + v.get().getClass().getName() + "]");
								}
							}
						} else {
							ConsoleJS.SERVER.error("- " + this + " // " + s + " [Not found!]");
						}
					}
				}
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
						gatherAllItemIDs(set, proxy.getEntry());
					}
				}
			}
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
	private final Function<ResourceLocation, Optional<T>> registry;
	private Map<ResourceLocation, TagWrapper<T>> tags;
	private int addedCount;
	private int removedCount;
	private List<Predicate<String>> globalPriorityList;
	private Registrar<T> registrar;

	public TagEventJS(String t, Map<ResourceLocation, Tag.Builder> m, Function<ResourceLocation, Optional<T>> r) {
		type = t;
		map = m;
		registry = r;
		addedCount = 0;
		removedCount = 0;
		globalPriorityList = null;
		registrar = null;

		switch (type) {
			case "items" -> registrar = UtilsJS.cast(KubeJSRegistries.items());
			case "blocks" -> registrar = UtilsJS.cast(KubeJSRegistries.blocks());
			case "fluids" -> registrar = UtilsJS.cast(KubeJSRegistries.fluids());
			case "entity_types" -> registrar = UtilsJS.cast(KubeJSRegistries.entityTypes());
		}
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
			var w = new TagWrapper<T>(this, entry.getKey(), entry.getValue());
			tags.put(entry.getKey(), w);
			ConsoleJS.SERVER.debug(type + "/#" + entry.getKey() + "; " + w.proxyList.size());
		}

		if (type.equals("items")) {
			for (var item : KubeJSObjects.ITEMS.values()) {
				for (var s : item.defaultTags) {
					add(s, item.id);
				}

				for (var block : KubeJSObjects.BLOCKS.values()) {
					if (block.itemBuilder != null) {
						for (var s : block.itemBuilder.defaultTags) {
							add(s, block.itemBuilder.id);
						}
					}
				}
			}
		} else if (type.equals("blocks")) {
			for (var block : KubeJSObjects.BLOCKS.values()) {
				for (var s : block.defaultTags) {
					add(s, block.id);
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

		if (ServerSettings.dataExport != null && registrar != null) {
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

		ConsoleJS.SERVER.info("[" + type + "] Found " + tags.size() + " tags, added " + addedCount + " objects, removed " + removedCount + " objects"/*, reordered " + reordered + " tags"*/);
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

	public TagWrapper<T> add(ResourceLocation tag, Object ids) {
		return get(tag).add(ids);
	}

	public TagWrapper<T> remove(ResourceLocation tag, Object ids) {
		return get(tag).remove(ids);
	}

	public TagWrapper<T> removeAll(ResourceLocation tag) {
		return get(tag).removeAll();
	}

	public void removeAllTagsFrom(Object ids) {
		for (var o : ListJS.orSelf(ids)) {
			var id = String.valueOf(o);

			for (var tagWrapper : tags.values()) {
				tagWrapper.proxyList.removeIf(proxy -> getIdOfEntry(proxy.entry().toString()).equals(id));
			}
		}
	}

	public void setGlobalPriorityList(@Nullable Object o) {
		globalPriorityList = parsePriorityList(o);
	}
}