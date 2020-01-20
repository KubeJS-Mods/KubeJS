package dev.latvian.kubejs.script.data;

import com.google.common.collect.Lists;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerJS;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.packs.DelegatableResourcePack;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class VirtualKubeJSDataPack extends DelegatableResourcePack
{
	public final boolean first;
	private final Map<ResourceLocation, String> locationToData;
	private final Map<String, String> pathToData;
	private final Set<String> namespaces;

	public VirtualKubeJSDataPack(boolean f)
	{
		super(new File("dummy"));
		first = f;
		locationToData = new HashMap<>();
		pathToData = new HashMap<>();
		namespaces = new HashSet<>();
	}

	public void addData(ResourceLocation id, String data)
	{
		locationToData.put(id, data);
		pathToData.put("data/" + id.getNamespace() + "/" + id.getPath(), data);
		namespaces.add(id.getNamespace());
	}

	public void resetData()
	{
		locationToData.clear();
		pathToData.clear();
		namespaces.clear();
	}

	@Override
	public InputStream getInputStream(String path) throws IOException
	{
		String s = pathToData.get(path);

		if (s != null)
		{
			if (ServerJS.instance.dataPackOutput)
			{
				ScriptType.SERVER.console.info("Served virtual file '" + path + "': " + s);
			}

			return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
		}

		throw new FileNotFoundException(path);
	}

	@Override
	public InputStream getResourceStream(ResourcePackType type, ResourceLocation location) throws IOException
	{
		String s = locationToData.get(location);

		if (s != null)
		{
			if (ServerJS.instance.dataPackOutput)
			{
				ScriptType.SERVER.console.info("Served virtual file '" + location + "': " + s);
			}

			return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
		}

		throw new FileNotFoundException(location.toString());
	}

	@Override
	public boolean resourceExists(String path)
	{
		return pathToData.containsKey(path);
	}

	@Override
	public boolean resourceExists(ResourcePackType type, ResourceLocation location)
	{
		return type == ResourcePackType.SERVER_DATA && locationToData.containsKey(location);
	}

	@Override
	public Collection<ResourceLocation> findResources(ResourcePackType type, String namespace, String path, int maxDepth, Predicate<String> filter)
	{
		List<ResourceLocation> list = Lists.newArrayList();

		for (ResourceLocation key : locationToData.keySet())
		{
			if (namespace.equals(key.getNamespace()))
			{
				try
				{
					int i = key.getPath().lastIndexOf('/');
					String p = i == -1 ? key.getPath() : key.getPath().substring(i + 1);

					if (key.getPath().startsWith(path) && filter.test(p))
					{
						list.add(key);
					}
				}
				catch (Exception ex)
				{
				}
			}
		}

		return list;
	}

	@Override
	public Set<String> getResourceNamespaces(ResourcePackType type)
	{
		return new HashSet<>(namespaces);
	}

	@Nullable
	@Override
	public <T> T getMetadata(IMetadataSectionSerializer<T> serializer)
	{
		return null;
	}

	@Override
	public String getName()
	{
		return "KubeJS Virtual Data Pack [First: " + first + "]";
	}

	@Override
	public void close()
	{
	}

	public boolean hasNamespace(String key)
	{
		return namespaces.contains(key);
	}
}
