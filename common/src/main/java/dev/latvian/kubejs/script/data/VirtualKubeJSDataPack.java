package dev.latvian.kubejs.script.data;

import com.google.common.collect.Lists;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerSettings;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import org.jetbrains.annotations.Nullable;

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
public class VirtualKubeJSDataPack extends AbstractPackResources
{
	public final boolean high;
	private final Map<ResourceLocation, String> locationToData;
	private final Map<String, String> pathToData;
	private final Set<String> namespaces;

	public VirtualKubeJSDataPack(boolean h)
	{
		super(new File("dummy"));
		high = h;
		locationToData = new HashMap<>();
		pathToData = new HashMap<>();
		namespaces = new HashSet<>();
	}

	public void addData(ResourceLocation id, String data)
	{
		locationToData.put(id, data);
		pathToData.put("data/" + id.getNamespace() + "/" + id.getPath(), data);
		namespaces.add(id.getNamespace());

		if (ServerSettings.instance.dataPackOutput)
		{
			ScriptType.SERVER.console.info("Registered virtual file [" + (high ? "high" : "low") + " priority] '" + id + "': " + data);
		}
	}

	@Override
	public InputStream getResource(String path) throws IOException
	{
		String s = pathToData.get(path);

		if (s != null)
		{
			if (ServerSettings.instance.dataPackOutput)
			{
				ScriptType.SERVER.console.info("Served virtual file [" + (high ? "high" : "low") + " priority] '" + path + "': " + s);
			}

			return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
		}

		throw new FileNotFoundException(path);
	}

	@Override
	public InputStream getResource(PackType type, ResourceLocation location) throws IOException
	{
		String s = locationToData.get(location);

		if (s != null)
		{
			if (ServerSettings.instance.dataPackOutput)
			{
				ScriptType.SERVER.console.info("Served virtual file [" + (high ? "high" : "low") + " priority] '" + location + "': " + s);
			}

			return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
		}

		throw new FileNotFoundException(location.toString());
	}

	@Override
	public boolean hasResource(String path)
	{
		return pathToData.containsKey(path);
	}

	@Override
	public boolean hasResource(PackType type, ResourceLocation location)
	{
		return type == PackType.SERVER_DATA && locationToData.containsKey(location);
	}

	@Override
	public Collection<ResourceLocation> getResources(PackType type, String namespace, String path, int maxDepth, Predicate<String> filter)
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
	public Set<String> getNamespaces(PackType type)
	{
		return new HashSet<>(namespaces);
	}

	@Nullable
	@Override
	public <T> T getMetadataSection(MetadataSectionSerializer<T> serializer)
	{
		return null;
	}

	@Override
	public String getName()
	{
		return "KubeJS Virtual Data Pack [" + (high ? "high" : "low") + " priority]";
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
