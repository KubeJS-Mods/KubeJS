package dev.latvian.kubejs.server;

import dev.latvian.kubejs.KubeJSCore;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
public class TagEventJS<T> extends ServerEventJS
{
	public static class TagWrapper<T>
	{
		private final TagEventJS<T> event;
		private final Tag<T> tag;

		private TagWrapper(TagEventJS<T> e, Tag<T> t)
		{
			event = e;
			tag = t;
		}

		public TagWrapper<T> add(Object ids)
		{
			for (Object o : ListJS.orSelf(ids))
			{
				String s = String.valueOf(o);

				if (s.startsWith("#"))
				{
					TagWrapper<T> w = event.get(s.substring(1));
					tag.getAllElements().addAll(w.tag.getAllElements());
					tag.getEntries().add(new Tag.TagEntry<>(w.tag));
					event.addedCount += w.tag.getAllElements().size();
					ScriptType.SERVER.console.logger.info("+ " + event.type + ":" + tag.getId() + " // " + s);
				}
				else
				{
					ResourceLocation id = new ResourceLocation(s);
					Optional<T> v = event.getter.apply(id);

					if (v.isPresent())
					{
						tag.getAllElements().add(v.get());
						tag.getEntries().add(new Tag.TagEntry<>(id));
						event.addedCount++;
						ScriptType.SERVER.console.logger.info("+ " + event.type + ":" + tag.getId() + " // " + s + " [" + v.get().getClass().getName() + "]");
					}
					else
					{
						ScriptType.SERVER.console.logger.warn("+ " + event.type + ":" + tag.getId() + " // " + s + " [Not found!]");
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
					tag.getAllElements().removeAll(w.tag.getAllElements());
					tag.getEntries().remove(new Tag.TagEntry<>(w.tag));
					event.removedCount += w.tag.getAllElements().size();
					ScriptType.SERVER.console.logger.info("- " + event.type + ":" + tag.getId() + " // " + s);
				}
				else
				{
					ResourceLocation id = new ResourceLocation(s);
					Optional<T> v = event.getter.apply(id);

					if (v.isPresent())
					{
						tag.getAllElements().remove(v.get());
						tag.getEntries().remove(new Tag.TagEntry<>(id));
						event.addedCount++;
						ScriptType.SERVER.console.logger.info("- " + event.type + ":" + tag.getId() + " // " + s + " [" + v.get().getClass().getName() + "]");
					}
					else
					{
						ScriptType.SERVER.console.logger.warn("- " + event.type + ":" + tag.getId() + " // " + s + " [Not found!]");
					}
				}
			}

			return this;
		}
	}

	private final TagCollection<T> tagCollection;
	private final String type;
	private final Function<ResourceLocation, Optional<T>> getter;
	private Map<ResourceLocation, TagWrapper<T>> tags;
	private int addedCount;
	private int removedCount;

	public TagEventJS(TagCollection<T> c, String t, Function<ResourceLocation, Optional<T>> g)
	{
		tagCollection = c;
		type = t;
		getter = g;
	}

	public String getType()
	{
		return type;
	}

	public void post()
	{
		tags = new HashMap<>();
		KubeJSCore.setTagMap(tagCollection, new HashMap<>(tagCollection.getTagMap()));

		for (Tag<T> tag : tagCollection.getTagMap().values())
		{
			tags.put(tag.getId(), new TagWrapper<>(this, tag));

			ScriptType.SERVER.console.logger.debug(type + "/#" + tag.getId());

			for (T v : tag.getAllElements())
			{
				ScriptType.SERVER.console.logger.debug("* " + (v instanceof IForgeRegistryEntry ? ((IForgeRegistryEntry) v).getRegistryName() : v));
			}
		}

		post(ScriptType.SERVER, KubeJSEvents.SERVER_DATAPACK_TAGS, type + "s");

		ScriptType.SERVER.console.logger.info("Found [" + type + "] " + tags.size() + " tags, added " + addedCount + ", removed " + removedCount);
	}

	public TagWrapper<T> get(Object tag)
	{
		ResourceLocation id = UtilsJS.getID(tag);
		TagWrapper<T> t = tags.get(id);

		if (t == null)
		{
			t = new TagWrapper<>(this, new Tag<>(id, new LinkedHashSet<>(), false));
			tags.put(id, t);
			tagCollection.getTagMap().put(id, t.tag);
		}

		return t;
	}
}