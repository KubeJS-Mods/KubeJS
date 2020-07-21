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
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author LatvianModder
 */
public class TagEventJS<T> extends EventJS
{
	public static class TagWrapper<T>
	{
		private final TagEventJS<T> event;
		private final ResourceLocation id;
		private final Tag.Builder builder;
		private final List<ITag.Proxy> proxyList;

		private TagWrapper(TagEventJS<T> e, ResourceLocation i, Tag.Builder t)
		{
			event = e;
			id = i;
			builder = t;
			proxyList = ((TagBuilderKJS) builder).getProxyListKJS();
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
					ScriptType.SERVER.console.info("+ " + event.type + ":" + id + " // " + w.id);
				}
				else
				{
					ResourceLocation sid = new ResourceLocation(s);
					Optional<T> v = event.registry.getValue(sid);

					if (v.isPresent())
					{
						builder.addItemEntry(sid, KubeJS.MOD_ID);
						event.addedCount++;
						ScriptType.SERVER.console.info("+ " + event.type + ":" + id + " // " + s + " [" + v.get().getClass().getName() + "]");
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
					ScriptType.SERVER.console.info("- " + event.type + ":" + id + " // " + w.id);
				}
				else
				{
					ResourceLocation sid = new ResourceLocation(s);
					Optional<T> v = event.registry.getValue(sid);

					if (v.isPresent())
					{
						ITag.ITagEntry entry = new ITag.ItemEntry(sid);
						proxyList.removeIf(p -> entry.equals(p.getEntry()));
						event.removedCount++;
						ScriptType.SERVER.console.info("- " + event.type + ":" + id + " // " + s + " [" + v.get().getClass().getName() + "]");
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
			ScriptType.SERVER.console.info("- " + event.type + ":" + id + " // (all)");
			event.removedCount += proxyList.size();
			proxyList.clear();
			return this;
		}
	}

	private final String type;
	private final Map<ResourceLocation, Tag.Builder> map;
	private final Registry<T> registry;
	private Map<ResourceLocation, TagWrapper<T>> tags;
	private int addedCount;
	private int removedCount;

	public TagEventJS(String t, Map<ResourceLocation, Tag.Builder> m, Registry<T> r)
	{
		type = t;
		map = m;
		registry = r;
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

		ScriptType.SERVER.console.info("[" + type + "] Found " + tags.size() + " tags, added " + addedCount + ", removed " + removedCount);
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
}