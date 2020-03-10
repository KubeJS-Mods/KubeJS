package dev.latvian.kubejs.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.script.data.VirtualKubeJSDataPack;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class TagEventJS<T> extends ServerEventJS
{
	public static class TagList<T>
	{
		private final TagEventJS<T> event;
		private final ResourceLocation id;
		private boolean replace;
		private final List<String> added;
		private final List<String> removed;

		private TagList(TagEventJS<T> e, ResourceLocation i)
		{
			event = e;
			id = i;
			replace = false;
			added = new ArrayList<>();
			removed = new ArrayList<>();
		}

		public TagList<T> replace()
		{
			replace = true;
			return this;
		}

		public TagList<T> add(Object... ids)
		{
			for (Object o : ids)
			{
				added.add(String.valueOf(o));
			}

			return this;
		}

		public TagList<T> remove(Object... ids)
		{
			for (Object o : ids)
			{
				removed.add(String.valueOf(o));
			}

			return this;
		}

		public JsonObject getFirst()
		{
			JsonObject json = new JsonObject();
			json.addProperty("replace", replace);

			JsonArray a = new JsonArray();

			for (String s : added)
			{
				a.add(s);
			}

			json.add("values", a);
			return json;
		}

		public JsonObject getLast()
		{
			JsonObject json = new JsonObject();
			json.addProperty("replace", false);
			JsonArray r = new JsonArray();

			for (String s : removed)
			{
				r.add(s);
			}

			json.add("values", new JsonArray());
			json.add("remove", r);
			return json;
		}
	}

	private final Registry<T> registry;
	//private final NetworkTagCollection<T> tagCollection;
	private final String collectionName;
	private final Map<ResourceLocation, TagList<T>> tags;

	public TagEventJS(Registry<T> r, String n, String tn)
	{
		registry = r;
		collectionName = n;
		tags = new HashMap<>();
	}

	public void loadAndPost(IResourceManager resourceManager, VirtualKubeJSDataPack first, VirtualKubeJSDataPack last)
	{
		/*
		tagCollection = new NetworkTagCollection<>(registry, "tags/" + collectionName, tn);
		tagCollection.reload(resourceManager, Runnable::run).thenAccept(tagCollection::registerAll);

		for (Tag<T> tag : tagCollection.getTagMap().values())
		{
			TagList list = new TagList(tag.getId());
			tags.put(list.id, list);

			for (T t : tag.getAllElements())
			{
				System.out.println("- " + t);
			}
		}
		*/

		post(ScriptType.SERVER, KubeJSEvents.SERVER_DATAPACK_TAGS + "." + collectionName);

		for (TagList list : tags.values())
		{
			if (!list.added.isEmpty())
			{
				first.addData(new ResourceLocation(list.id.getNamespace(), "tags/" + collectionName + "/" + list.id.getPath() + ".json"), list.getFirst().toString());
			}

			if (!list.removed.isEmpty())
			{
				last.addData(new ResourceLocation(list.id.getNamespace(), "tags/" + collectionName + "/" + list.id.getPath() + ".json"), list.getLast().toString());
			}
		}

		//Map<ResourceLocation, Tag.Builder<T>> map = new HashMap<>();
		//tagCollection.registerAll(map);
		//return tagCollection;
	}

	public String getCollectionName()
	{
		return collectionName;
	}

	public TagList<T> get(Object tag)
	{
		ResourceLocation location = UtilsJS.getID(tag);
		TagList<T> list = tags.get(location);

		if (list == null)
		{
			list = new TagList<>(this, location);
			tags.put(location, list);
		}

		return list;
	}
}