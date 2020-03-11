package dev.latvian.kubejs.server;

import com.google.common.collect.ImmutableMap;
import dev.latvian.kubejs.KubeJSCore;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.tags.NetworkTagCollection;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author LatvianModder
 */
public class TagEventJS<T> extends ServerEventJS
{
	public static class TagList<T>
	{
		private final TagEventJS<T> event;
		private final ResourceLocation id;
		private final List<T> added;
		private final List<T> removed;

		private TagList(TagEventJS<T> e, ResourceLocation _id)
		{
			event = e;
			id = _id;
			added = new ArrayList<>();
			removed = new ArrayList<>();
		}

		public TagList add(Object ids)
		{
			for (Object o : ListJS.orSelf(ids))
			{
				String s = String.valueOf(o);

				if (s.startsWith("#"))
				{
					Tag<T> tag = event.tagMap.get(new ResourceLocation(s.substring(1)));

					if (tag != null)
					{
						added.addAll(tag.getAllElements());
					}
				}
				else
				{
					event.registry.getValue(new ResourceLocation(s)).ifPresent(added::add);
				}
			}

			return this;
		}

		public TagList add(Object... ids)
		{
			return add(Arrays.asList(ids));
		}

		public TagList remove(Object ids)
		{
			for (Object o : ListJS.orSelf(ids))
			{
				String s = String.valueOf(o);

				if (s.startsWith("#"))
				{
					Tag<T> tag = event.tagMap.get(new ResourceLocation(s.substring(1)));

					if (tag != null)
					{
						removed.addAll(tag.getAllElements());
					}
				}
				else
				{
					event.registry.getValue(new ResourceLocation(s)).ifPresent(removed::add);
				}
			}

			return this;
		}

		public TagList remove(Object... ids)
		{
			return remove(Arrays.asList(ids));
		}
	}

	private final TagGroup<T> group;

	private Map<ResourceLocation, TagList<T>> tags;
	private Registry<T> registry;
	private Map<ResourceLocation, Tag<T>> tagMap;

	public TagEventJS(TagGroup<T> g)
	{
		group = g;
	}

	public String getCollectionName()
	{
		return group.name;
	}

	int[] post(NetworkTagManager manager)
	{
		int[] count = new int[3];
		tags = new HashMap<>();
		registry = group.registrySupplier.get();
		NetworkTagCollection<T> tagCollection = group.collectionGetter.apply(manager);

		tagMap = new HashMap<>(tagCollection.getTagMap());
		count[0] += tagMap.size();

		for (Tag<T> tag : tagMap.values())
		{
			ScriptType.SERVER.console.logger.debug(group.name + "/#" + tag.getId());

			for (T v : tag.getAllElements())
			{
				ScriptType.SERVER.console.logger.debug("* " + registry.getKey(v));
			}
		}

		post(ScriptType.SERVER, KubeJSEvents.SERVER_DATAPACK_TAGS, group.name);

		List<TagList<T>> list = new ArrayList<>();

		for (TagList<T> l : tags.values())
		{
			if (!l.added.isEmpty() || !l.removed.isEmpty())
			{
				list.add(l);
			}
		}

		if (!list.isEmpty())
		{
			for (TagEventJS.TagList<T> tagList : list)
			{
				Tag<T> tag = tagMap.computeIfAbsent(tagList.id, Tag::new);
				Set<T> taggedItems = new LinkedHashSet<>(tag.getAllElements());

				ScriptType.SERVER.console.logger.debug(group.name + "/#" + tag.getId());

				for (T v : tagList.added)
				{
					ScriptType.SERVER.console.logger.debug("+ " + v);
					taggedItems.add(v);
					count[1]++;
				}

				for (T v : tagList.removed)
				{
					ScriptType.SERVER.console.logger.debug("- " + v);
					taggedItems.remove(v);
					count[2]++;
				}

				KubeJSCore.setTaggedItems(tag, taggedItems);
				KubeJSCore.setEntries(tag, Collections.singleton(new Tag.ListEntry<>(taggedItems)));
			}

			KubeJSCore.setTagMap(tagCollection, ImmutableMap.copyOf(tagMap));
			group.collectionSetter.accept(tagCollection);
			ScriptType.SERVER.console.logger.debug("Found [" + group.name + "] " + count[0] + " tags, added " + count[1] + ", removed " + count[2]);
		}

		return count;
	}

	public TagList<T> get(Object tag)
	{
		return tags.computeIfAbsent(UtilsJS.getID(tag), id -> new TagList<>(this, id));
	}
}