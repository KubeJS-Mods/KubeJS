package dev.latvian.kubejs.server;

import dev.latvian.kubejs.core.TagBuilderKJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
public class TagEventJS<T> extends ServerEventJS
{
	public static class TagWrapper<T>
	{
		private final TagEventJS<T> event;
		private final ResourceLocation id;
		private final Tag.Builder<T> tag;
		private final Set<Tag.ITagEntry<T>> entries;

		private TagWrapper(TagEventJS<T> e, ResourceLocation i, Tag.Builder<T> t)
		{
			event = e;
			id = i;
			tag = t;
			entries = ((TagBuilderKJS<T>) tag).getEntriesKJS();
		}

		public TagWrapper<T> add(Object ids)
		{
			for (Object o : ListJS.orSelf(ids))
			{
				String s = String.valueOf(o);

				if (s.startsWith("#"))
				{
					TagWrapper<T> w = event.get(s.substring(1));
					entries.add(new Tag.TagEntry<>(w.id));
					event.addedCount += w.entries.size();
					ScriptType.SERVER.console.logger.info("+ " + event.type + ":" + id + " // " + w.id);
				}
				else
				{
					ResourceLocation sid = new ResourceLocation(s);
					Optional<T> v = event.getter.apply(sid);

					if (v.isPresent())
					{
						entries.add(new Tag.ListEntry<>(Collections.singleton(v.get())));
						event.addedCount++;
						ScriptType.SERVER.console.logger.info("+ " + event.type + ":" + id + " // " + s + " [" + v.get().getClass().getName() + "]");
					}
					else
					{
						ScriptType.SERVER.console.logger.warn("+ " + event.type + ":" + id + " // " + s + " [Not found!]");
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
					entries.remove(new Tag.TagEntry<>(w.id));
					event.addedCount += w.entries.size();
					ScriptType.SERVER.console.logger.info("- " + event.type + ":" + id + " // " + w.id);
				}
				else
				{
					ResourceLocation sid = new ResourceLocation(s);
					Optional<T> v = event.getter.apply(sid);

					if (v.isPresent())
					{
						entries.remove(new Tag.ListEntry<>(Collections.singleton(v.get())));
						event.addedCount++;
						ScriptType.SERVER.console.logger.info("- " + event.type + ":" + id + " // " + s + " [" + v.get().getClass().getName() + "]");
					}
					else
					{
						ScriptType.SERVER.console.logger.warn("- " + event.type + ":" + id + " // " + s + " [Not found!]");
					}
				}
			}

			return this;
		}
	}

	private final String type;
	private final Map<ResourceLocation, Tag.Builder<T>> map;
	private final Function<ResourceLocation, Optional<T>> getter;
	private Map<ResourceLocation, TagWrapper<T>> tags;
	private int addedCount;
	private int removedCount;

	public TagEventJS(String t, Map<ResourceLocation, Tag.Builder<T>> m, Function<ResourceLocation, Optional<T>> g)
	{
		type = t;
		map = m;
		getter = g;
	}

	public String getType()
	{
		return type;
	}

	public void post(String event)
	{
		tags = new HashMap<>();

		for (Map.Entry<ResourceLocation, Tag.Builder<T>> entry : map.entrySet())
		{
			TagWrapper<T> w = new TagWrapper<>(this, entry.getKey(), entry.getValue());
			tags.put(entry.getKey(), w);
			ScriptType.SERVER.console.logger.debug(type + "/#" + entry.getKey() + "; " + w.entries.size());
		}

		ScriptType.SERVER.console.setLineNumber(true);
		post(ScriptType.SERVER, event);
		post(ScriptType.SERVER, "server.datapack.tags." + type); //TODO: To be removed
		ScriptType.SERVER.console.setLineNumber(false);

		ScriptType.SERVER.console.logger.info("[" + type + "] Found " + tags.size() + " tags, added " + addedCount + ", removed " + removedCount);
	}

	public TagWrapper<T> get(Object tag)
	{
		ResourceLocation id = UtilsJS.getID(tag);
		TagWrapper<T> t = tags.get(id);

		if (t == null)
		{
			t = new TagWrapper<>(this, id, new Tag.Builder<>());
			tags.put(id, t);
			map.put(id, t.tag);
		}

		return t;
	}
}