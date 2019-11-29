package dev.latvian.kubejs.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.documentation.Ignore;
import dev.latvian.kubejs.script.data.VirtualKubeJSDataPack;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class TagEventJS extends ServerEventJS
{
	public static class TagClass
	{
		private final String name;
		private final Map<ResourceLocation, TagList> tags;

		private TagClass(String n)
		{
			name = n;
			tags = new HashMap<>();
		}

		public TagList get(Object tag)
		{
			ResourceLocation location = UtilsJS.getID(tag);
			TagList list = tags.get(location);

			if (list == null)
			{
				list = new TagList(this, location);
				tags.put(location, list);
			}

			return list;
		}
	}

	public static class TagList
	{
		private final TagClass tagClass;
		private final ResourceLocation id;
		private final List<String> values;

		private TagList(TagClass c, ResourceLocation i)
		{
			tagClass = c;
			id = i;
			values = new ArrayList<>();
		}

		public void add(String... value)
		{
			values.addAll(Arrays.asList(value));
		}

		@Override
		public String toString()
		{
			JsonObject json = new JsonObject();
			json.addProperty("replace", false);

			JsonArray a = new JsonArray();

			for (String s : values)
			{
				a.add(s);
			}

			json.add("values", a);

			return json.toString();
		}
	}

	private final IResourceManager resourceManager;
	private final Map<String, TagClass> tags;

	public TagEventJS(IResourceManager m)
	{
		resourceManager = m;
		tags = new HashMap<String, TagClass>()
		{
			@Override
			public TagClass get(Object key)
			{
				TagClass file = super.get(key);

				if (file == null)
				{
					file = new TagClass(key.toString());
					put(file.name, file);
				}

				return file;
			}

			@Override
			public boolean containsKey(Object key)
			{
				return true;
			}
		};
	}

	public Map<String, TagClass> getTags()
	{
		return tags;
	}

	@Ignore
	public void addDataToPack(VirtualKubeJSDataPack pack)
	{
		for (TagClass tagClass : tags.values())
		{
			for (TagList list : tagClass.tags.values())
			{
				pack.addData(new ResourceLocation(list.id.getNamespace(), "tags/" + tagClass.name + "/" + list.id.getPath() + ".json"), list.toString());
			}
		}
	}
}