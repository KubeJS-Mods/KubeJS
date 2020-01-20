package dev.latvian.kubejs.script.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.script.ScriptType;
import net.minecraft.item.BlockItem;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackFileNotFoundException;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class KubeJSResourcePack implements IResourcePack
{
	private final File folder;
	private final ResourcePackType packType;

	public KubeJSResourcePack(File f, ResourcePackType t)
	{
		folder = f;
		packType = t;
	}

	private static String getFullPath(ResourcePackType type, ResourceLocation location)
	{
		return String.format("%s/%s/%s", type.getDirectoryName(), location.getNamespace(), location.getPath());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public InputStream getRootResourceStream(String fileName) throws IOException
	{
		if (fileName.equals("pack.png"))
		{
			return KubeJSResourcePack.class.getResourceAsStream("/kubejs_logo.png");
		}

		throw new ResourcePackFileNotFoundException(folder, fileName);
	}

	@Override
	public InputStream getResourceStream(ResourcePackType type, ResourceLocation location) throws IOException
	{
		String resourcePath = getFullPath(type, location);

		if (type != packType)
		{
			throw new IllegalStateException(packType.getDirectoryName() + " KubeJS pack can't load " + resourcePath + "!");
		}

		File file = new File(folder, resourcePath);

		if (file.exists())
		{
			return new BufferedInputStream(new FileInputStream(file));
		}
		else if (packType == ResourcePackType.CLIENT_RESOURCES && location.getNamespace().equals(KubeJS.MOD_ID))
		{
			if (location.getPath().endsWith(".json"))
			{
				JsonObject json = new JsonObject();

				if (generateJsonFile(location.getPath().substring(0, location.getPath().length() - 5), json))
				{
					return new ByteArrayInputStream(json.toString().getBytes(StandardCharsets.UTF_8));
				}
			}
		}

		throw new ResourcePackFileNotFoundException(folder, resourcePath);
	}

	private boolean generateJsonFile(String path, JsonObject json)
	{
		if (path.startsWith("models/item/"))
		{
			String s = path.substring(12);

			if (ForgeRegistries.ITEMS.getValue(new ResourceLocation(KubeJS.MOD_ID, s)) instanceof BlockItem)
			{
				json.addProperty("parent", KubeJS.MOD_ID + ":block/" + s);
			}
			else
			{
				json.addProperty("parent", "item/generated");
				JsonObject textures = new JsonObject();
				textures.addProperty("layer0", KubeJS.MOD_ID + ":item/" + s);
				json.add("textures", textures);
			}

			return true;
		}
		else if (path.startsWith("models/block/"))
		{
			json.addProperty("parent", "block/cube_all");
			JsonObject textures = new JsonObject();
			textures.addProperty("all", KubeJS.MOD_ID + ":block/" + path.substring(13));
			json.add("textures", textures);
			return true;
		}
		else if (path.startsWith("blockstates/"))
		{
			JsonObject variants = new JsonObject();
			JsonObject model = new JsonObject();
			model.addProperty("model", KubeJS.MOD_ID + ":block/" + path.substring(12));
			variants.add("", model);
			json.add("variants", variants);
			return true;
		}

		return false;
	}

	@Override
	public boolean resourceExists(ResourcePackType type, ResourceLocation location)
	{
		if (packType == ResourcePackType.CLIENT_RESOURCES && location.getNamespace().equals(KubeJS.MOD_ID))
		{
			if (location.getPath().endsWith(".json") && generateJsonFile(location.getPath().substring(0, location.getPath().length() - 5), new JsonObject()))
			{
				return true;
			}
		}

		return type == packType && new File(folder, getFullPath(type, location)).exists();
	}

	@Override
	public Collection<ResourceLocation> findResources(ResourcePackType type, String namespace, String path, int maxDepth, Predicate<String> filter)
	{
		if (type != packType)
		{
			return Collections.emptySet();
		}

		File file1 = new File(folder, type.getDirectoryName());
		List<ResourceLocation> list = Lists.newArrayList();

		findResources0(new File(new File(file1, namespace), path), maxDepth, namespace, list, path, filter);

		return list;
	}

	private void findResources0(File file, int maxDepth, String namespace, List<ResourceLocation> list, String path, Predicate<String> filter)
	{
		File[] files = file.listFiles();

		if (files == null || files.length == 0)
		{
			return;
		}

		for (File f : files)
		{
			if (f.isDirectory())
			{
				if (maxDepth > 0)
				{
					findResources0(f, maxDepth - 1, namespace, list, path + f.getName() + "/", filter);
				}
			}
			else if (!f.getName().endsWith(".mcmeta") && filter.test(f.getName()))
			{
				try
				{
					list.add(new ResourceLocation(namespace, path + f.getName()));
				}
				catch (ResourceLocationException ex)
				{
					(packType == ResourcePackType.CLIENT_RESOURCES ? ScriptType.CLIENT : ScriptType.SERVER).console.error(ex.getMessage());
				}
			}
		}
	}

	@Override
	public Set<String> getResourceNamespaces(ResourcePackType type)
	{
		if (type != packType)
		{
			return Collections.emptySet();
		}

		File file = new File(folder, type.getDirectoryName());

		if (file.exists() && file.isDirectory())
		{
			File[] list = file.listFiles();

			if (list != null && list.length > 0)
			{
				HashSet<String> namespaces = new HashSet<>();

				for (File f : list)
				{
					if (f.isDirectory())
					{
						namespaces.add(f.getName().toLowerCase());
					}
				}

				return namespaces;
			}
		}

		return Collections.emptySet();
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
		return "KubeJS Resource Pack";
	}

	@Override
	public void close()
	{
	}
}
