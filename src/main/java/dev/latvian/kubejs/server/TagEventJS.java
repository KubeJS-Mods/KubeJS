package dev.latvian.kubejs.server;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.core.TagBuilderKJS;
import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.tags.ITag;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
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
public class TagEventJS<T> extends EventJS
{
	@Nullable
	private static List<Predicate<String>> parsePriorityList(@Nullable Object o)
	{
		if (o == null)
		{
			return null;
		}

		List<Predicate<String>> list = new ArrayList<>();

		for (Object o1 : ListJS.orSelf(o))
		{
			String s = String.valueOf(o1);

			if (s.startsWith("@"))
			{
				String m = s.substring(1);
				list.add(id -> id.startsWith(m));
			}
			else if (s.startsWith("!@"))
			{
				String m = s.substring(2);
				list.add(id -> !id.startsWith(m));
			}
			else
			{
				list.add(id -> id.equals(s));
			}
		}

		return list.isEmpty() ? null : list;
	}

	public static class TagWrapper<T>
	{
		private final TagEventJS<T> event;
		private final ResourceLocation id;
		private final Tag.Builder builder;
		private final List<ITag.Proxy> proxyList;
		private List<Predicate<String>> priorityList;

		private TagWrapper(TagEventJS<T> e, ResourceLocation i, Tag.Builder t)
		{
			event = e;
			id = i;
			builder = t;
			proxyList = ((TagBuilderKJS) builder).getProxyListKJS();
			priorityList = null;
		}

		public TagWrapper<T> add(Object ids)
		{
			for (Object o : ListJS.orSelf(ids))
			{
				String s = String.valueOf(o);

				if (s.startsWith("#"))
				{
					TagWrapper<T> w = event.get(s.substring(1));
					builder.addTagEntry(w.id, KubeJS.MOD_ID);
					event.addedCount += w.proxyList.size();
					ScriptType.SERVER.console.debug("+ " + event.type + ":" + id + " // " + w.id);
				}
				else
				{
					ResourceLocation sid = new ResourceLocation(s);
					Optional<T> v = event.registry.apply(sid);

					if (v.isPresent())
					{
						builder.addItemEntry(sid, KubeJS.MOD_ID);
						event.addedCount++;
						ScriptType.SERVER.console.debug("+ " + event.type + ":" + id + " // " + s + " [" + v.get().getClass().getName() + "]");
					}
					else
					{
						ScriptType.SERVER.console.warn("+ " + event.type + ":" + id + " // " + s + " [Not found!]");
					}
				}
			}

			return this;
		}

		public TagWrapper<T> remove(Object ids)
		{
			for (Object o : ListJS.orSelf(ids))
			{
				String s = String.valueOf(o);

				if (s.startsWith("#"))
				{
					TagWrapper<T> w = event.get(s.substring(1));
					ITag.ITagEntry entry = new ITag.TagEntry(w.id);
					proxyList.removeIf(p -> entry.equals(p.getEntry()));
					event.removedCount += w.proxyList.size();
					ScriptType.SERVER.console.debug("- " + event.type + ":" + id + " // " + w.id);
				}
				else
				{
					ResourceLocation sid = new ResourceLocation(s);
					Optional<T> v = event.registry.apply(sid);

					if (v.isPresent())
					{
						ITag.ITagEntry entry = new ITag.ItemEntry(sid);
						proxyList.removeIf(p -> entry.equals(p.getEntry()));
						event.removedCount++;
						ScriptType.SERVER.console.debug("- " + event.type + ":" + id + " // " + s + " [" + v.get().getClass().getName() + "]");
					}
					else
					{
						ScriptType.SERVER.console.warn("- " + event.type + ":" + id + " // " + s + " [Not found!]");
					}
				}
			}

			return this;
		}

		public TagWrapper<T> removeAll()
		{
			ScriptType.SERVER.console.debug("- " + event.type + ":" + id + " // (all)");
			event.removedCount += proxyList.size();
			proxyList.clear();
			return this;
		}

		public TagWrapper<T> setPriorityList(@Nullable Object o)
		{
			priorityList = parsePriorityList(o);
			return this;
		}

		private void gatherAllItemIDs(HashSet<String> set, ITag.ITagEntry entry)
		{
			if (entry instanceof ITag.ItemEntry)
			{
				set.add(((ITag.ItemEntry) entry).toString());
			}
			else if (entry instanceof ITag.TagEntry)
			{
				TagWrapper<T> w = event.tags.get(new ResourceLocation(entry.toString().substring(1)));

				if (w != null && w != this)
				{
					for (ITag.Proxy proxy : w.proxyList)
					{
						gatherAllItemIDs(set, proxy.getEntry());
					}
				}
			}
		}

		/**
		 * Yes its not as efficient as it could be, but this doesn't get run too often. This way it keeps order of original tags.
		 */
		public boolean sort()
		{
			List<List<ITag.Proxy>> listOfLists = new ArrayList<>();

			for (int i = 0; i < priorityList.size() + 1; i++)
			{
				listOfLists.add(new ArrayList<>());
			}

			for (ITag.Proxy proxy : proxyList)
			{
				boolean added = false;

				HashSet<String> set = new HashSet<>();
				gatherAllItemIDs(set, proxy.getEntry());

				for (String id : set)
				{
					for (int i = 0; i < priorityList.size(); i++)
					{
						if (priorityList.get(i).test(id))
						{
							listOfLists.get(i).add(proxy);
							added = true;
							break;
						}
					}
				}

				if (!added)
				{
					listOfLists.get(priorityList.size()).add(proxy);
				}
			}

			List<ITag.Proxy> proxyList0 = new ArrayList<>(proxyList);

			proxyList.clear();

			for (List<ITag.Proxy> list : listOfLists)
			{
				proxyList.addAll(list);
			}

			if (!proxyList0.equals(proxyList))
			{
				ScriptType.SERVER.console.debug("* " + event.type + ":" + id);
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

	public TagEventJS(String t, Map<ResourceLocation, Tag.Builder> m, Function<ResourceLocation, Optional<T>> r)
	{
		type = t;
		map = m;
		registry = r;
		addedCount = 0;
		removedCount = 0;
		globalPriorityList = null;
	}

	public String getType()
	{
		return type;
	}

	public void post(String event)
	{
		tags = new HashMap<>();

		for (Map.Entry<ResourceLocation, Tag.Builder> entry : map.entrySet())
		{
			TagWrapper<T> w = new TagWrapper<>(this, entry.getKey(), entry.getValue());
			tags.put(entry.getKey(), w);
			ScriptType.SERVER.console.debug(type + "/#" + entry.getKey() + "; " + w.proxyList.size());
		}

		ScriptType.SERVER.console.setLineNumber(true);
		post(ScriptType.SERVER, event);
		ScriptType.SERVER.console.setLineNumber(false);

		int reordered = 0;

		for (TagWrapper<T> wrapper : tags.values())
		{
			if (wrapper.priorityList == null)
			{
				wrapper.priorityList = globalPriorityList;
			}

			if (wrapper.priorityList != null && wrapper.sort())
			{
				reordered++;
			}
		}

		ScriptType.SERVER.console.info("[" + type + "] Found " + tags.size() + " tags, added " + addedCount + " objects, removed " + removedCount + " objects"/*, reordered " + reordered + " tags"*/);
	}

	public TagWrapper<T> get(@ID String tag)
	{
		ResourceLocation id = UtilsJS.getMCID(tag);
		TagWrapper<T> t = tags.get(id);

		if (t == null)
		{
			t = new TagWrapper<>(this, id, Tag.Builder.create());
			tags.put(id, t);
			map.put(id, t.builder);
		}

		return t;
	}

	public void setGlobalPriorityList(@Nullable Object o)
	{
		globalPriorityList = parsePriorityList(o);
	}
}