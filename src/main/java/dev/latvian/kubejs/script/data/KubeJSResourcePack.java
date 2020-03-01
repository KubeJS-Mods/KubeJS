package dev.latvian.kubejs.script.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.block.BlockJS;
import dev.latvian.kubejs.item.BlockItemJS;
import dev.latvian.kubejs.item.ItemJS;
import dev.latvian.kubejs.script.ScriptType;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackFileNotFoundException;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
		else if (location.getNamespace().equals(KubeJS.MOD_ID))
		{
			if (location.getPath().endsWith(".json"))
			{
				JsonObject json = new JsonObject();
				String p = location.getPath().substring(0, location.getPath().length() - 5);

				if (packType == ResourcePackType.CLIENT_RESOURCES ? generateClientJsonFile(p, json) : generateServerJsonFile(p, json))
				{
					return new ByteArrayInputStream(json.toString().getBytes(StandardCharsets.UTF_8));
				}
			}
		}

		throw new ResourcePackFileNotFoundException(folder, resourcePath);
	}

	@Override
	public boolean resourceExists(ResourcePackType type, ResourceLocation location)
	{
		if (location.getPath().endsWith(".json") && location.getNamespace().equals(KubeJS.MOD_ID))
		{
			String p = location.getPath().substring(0, location.getPath().length() - 5);

			if (packType == ResourcePackType.CLIENT_RESOURCES ? generateClientJsonFile(p, new JsonObject()) : generateServerJsonFile(p, new JsonObject()))
			{
				return true;
			}
		}

		return type == packType && new File(folder, getFullPath(type, location)).exists();
	}

	private boolean generateClientJsonFile(String path, JsonObject json)
	{
		if (path.startsWith("models/item/"))
		{
			ResourceLocation id = new ResourceLocation(KubeJS.MOD_ID, path.substring(12));
			ItemJS item = ItemJS.KUBEJS_ITEMS.get(id);

			if (item == null)
			{
				BlockItemJS blockItem = BlockItemJS.KUBEJS_BLOCK_ITEMS.get(id);

				if (blockItem != null)
				{
					json.addProperty("parent", blockItem.properties.parentModel);
				}
				else
				{
					json.addProperty("parent", KubeJS.MOD_ID + ":block/" + id.getPath());
				}
			}
			else
			{
				json.addProperty("parent", item.properties.parentModel);

				if (item.properties.parentModel.equals("item/generated"))
				{
					JsonObject textures = new JsonObject();
					textures.addProperty("layer0", item.properties.texture);
					json.add("textures", textures);
				}
			}

			return true;
		}
		else if (path.startsWith("models/block/"))
		{
			BlockJS block = BlockJS.KUBEJS_BLOCKS.get(new ResourceLocation(KubeJS.MOD_ID, path.substring(13)));

			String particle = block.properties.textures.get("particle").getAsString();

			if (areAllTexturesEqual(block.properties.textures, particle))
			{
				json.addProperty("parent", "block/cube_all");
				JsonObject textures = new JsonObject();
				textures.addProperty("all", particle);
				json.add("textures", textures);
			}
			else
			{
				json.addProperty("parent", "block/cube");
				json.add("textures", block.properties.textures);
			}

			if (!block.properties.color.isEmpty())
			{
				JsonObject cube = new JsonObject();
				JsonArray from = new JsonArray();
				from.add(0);
				from.add(0);
				from.add(0);
				cube.add("from", from);
				JsonArray to = new JsonArray();
				to.add(16);
				to.add(16);
				to.add(16);
				cube.add("to", to);
				JsonObject faces = new JsonObject();

				for (Direction direction : Direction.values())
				{
					JsonObject f = new JsonObject();
					f.addProperty("texture", "#" + direction.getName());
					f.addProperty("cullface", direction.getName());
					f.addProperty("tintindex", 0);
					faces.add(direction.getName(), f);
				}

				cube.add("faces", faces);

				JsonArray elements = new JsonArray();
				elements.add(cube);
				json.add("elements", elements);
			}

			return true;
		}
		else if (path.startsWith("blockstates/"))
		{
			BlockJS block = BlockJS.KUBEJS_BLOCKS.get(new ResourceLocation(KubeJS.MOD_ID, path.substring(12)));
			JsonObject variants = new JsonObject();
			JsonObject model = new JsonObject();
			model.addProperty("model", block.properties.model);
			variants.add("", model);
			json.add("variants", variants);
			return true;
		}

		return false;
	}

	private boolean areAllTexturesEqual(JsonObject tex, String t)
	{
		for (Direction direction : Direction.values())
		{
			if (!tex.get(direction.getName()).getAsString().equals(t))
			{
				return false;
			}
		}

		return true;
	}

	private boolean generateServerJsonFile(String path, JsonObject json)
	{
		return false;
	}

	@Override
	public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String namespace, String path, int maxDepth, Predicate<String> filter)
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

		HashSet<String> namespaces = new HashSet<>();

		if (!ItemJS.KUBEJS_ITEMS.isEmpty() || BlockItemJS.KUBEJS_BLOCK_ITEMS.isEmpty() || !BlockJS.KUBEJS_BLOCKS.isEmpty())
		{
			namespaces.add(KubeJS.MOD_ID);
		}

		File file = new File(folder, type.getDirectoryName());

		if (file.exists() && file.isDirectory())
		{
			File[] list = file.listFiles();

			if (list != null && list.length > 0)
			{
				for (File f : list)
				{
					if (f.isDirectory())
					{
						namespaces.add(f.getName().toLowerCase());
					}
				}
			}
		}

		return namespaces;
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
