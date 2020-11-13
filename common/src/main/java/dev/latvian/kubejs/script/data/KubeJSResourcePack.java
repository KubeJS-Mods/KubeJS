package dev.latvian.kubejs.script.data;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSObjects;
import dev.latvian.kubejs.KubeJSPaths;
import dev.latvian.kubejs.block.BlockBuilder;
import dev.latvian.kubejs.fluid.FluidBuilder;
import dev.latvian.kubejs.item.ItemBuilder;
import dev.latvian.kubejs.util.BuilderBase;
import dev.latvian.kubejs.util.UtilsJS;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.ResourcePackFileNotFoundException;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class KubeJSResourcePack implements PackResources
{
	private final PackType packType;

	public KubeJSResourcePack(PackType t)
	{
		packType = t;
	}

	private static String getFullPath(PackType type, ResourceLocation location)
	{
		return String.format("%s/%s/%s", type.getDirectory(), location.getNamespace(), location.getPath());
	}

	@Override
	@Environment(EnvType.CLIENT)
	public InputStream getRootResource(String fileName) throws IOException
	{
		if (fileName.equals("pack.png"))
		{
			return KubeJSResourcePack.class.getResourceAsStream("/kubejs_logo.png");
		}

		throw new ResourcePackFileNotFoundException(KubeJSPaths.DIRECTORY.toFile(), fileName);
	}

	@Override
	public InputStream getResource(PackType type, ResourceLocation location) throws IOException
	{
		String resourcePath = getFullPath(type, location);

		if (type != packType)
		{
			throw new IllegalStateException(packType.getDirectory() + " KubeJS pack can't load " + resourcePath + "!");
		}

		Path file = KubeJSPaths.DIRECTORY.resolve(resourcePath);

		if (Files.exists(file))
		{
			return Files.newInputStream(file);
		}
		else
		{
			if (location.getPath().endsWith(".json"))
			{
				JsonObject json = new JsonObject();
				String p = location.getPath().substring(0, location.getPath().length() - 5);

				if (packType == PackType.CLIENT_RESOURCES ? generateClientJsonFile(location.getNamespace(), p, json, true) : generateServerJsonFile(location.getNamespace(), p, json, true))
				{
					return new ByteArrayInputStream(json.toString().getBytes(StandardCharsets.UTF_8));
				}
			}
		}

		throw new ResourcePackFileNotFoundException(KubeJSPaths.DIRECTORY.toFile(), resourcePath);
	}

	@Override
	public boolean hasResource(PackType type, ResourceLocation location)
	{
		if (location.getPath().endsWith(".json"))
		{
			String p = location.getPath().substring(0, location.getPath().length() - 5);

			if (packType == PackType.CLIENT_RESOURCES ? generateClientJsonFile(location.getNamespace(), p, new JsonObject(), false) : generateServerJsonFile(location.getNamespace(), p, new JsonObject(), false))
			{
				return true;
			}
		}

		return type == packType && Files.exists(KubeJSPaths.DIRECTORY.resolve(getFullPath(type, location)));
	}

	private boolean generateClientJsonFile(String namespace, String path, JsonObject json, boolean real)
	{
		if (namespace.equals(KubeJS.MOD_ID) && path.equals("lang/en_us"))
		{
			if (real)
			{
				for (BuilderBase builder : KubeJSObjects.ALL)
				{
					if (!builder.displayName.isEmpty())
					{
						json.addProperty(builder.translationKey, builder.displayName);
					}
				}

				for (FluidBuilder builder : KubeJSObjects.FLUIDS.values())
				{
					if (!builder.displayName.isEmpty())
					{
						json.addProperty(builder.bucketItem.getDescriptionId(), builder.displayName + " Bucket");
					}
				}
			}

			return true;
		}
		else if (path.startsWith("models/item/"))
		{
			ResourceLocation id = new ResourceLocation(namespace, path.substring(12));
			ItemBuilder builder = KubeJSObjects.ITEMS.get(id);

			if (builder == null)
			{
				BlockBuilder blockBuilder = KubeJSObjects.BLOCKS.get(id);

				if (blockBuilder == null)
				{
					if (path.endsWith("_bucket"))
					{
						FluidBuilder fluidBuilder = KubeJSObjects.FLUIDS.get(new ResourceLocation(namespace, path.substring(12, path.length() - 7)));

						if (fluidBuilder != null)
						{
							json.addProperty("parent", "kubejs:item/generated_bucket");
							return true;
						}
					}
				}
				else if (blockBuilder.itemBuilder != null)
				{
					if (real)
					{
						json.addProperty("parent", blockBuilder.itemBuilder.parentModel);
					}

					return true;
				}
			}
			else
			{
				if (real)
				{
					json.addProperty("parent", builder.parentModel);

					if (builder.parentModel.equals("item/generated"))
					{
						JsonObject textures = new JsonObject();
						textures.addProperty("layer0", builder.texture);
						json.add("textures", textures);
					}
				}

				return true;
			}
		}
		else if (path.startsWith("models/block/"))
		{
			BlockBuilder builder = KubeJSObjects.BLOCKS.get(new ResourceLocation(namespace, path.substring(13)));

			if (builder != null)
			{
				if (real)
				{
					String particle = builder.textures.get("particle").getAsString();

					if (areAllTexturesEqual(builder.textures, particle))
					{
						json.addProperty("parent", "block/cube_all");
						JsonObject textures = new JsonObject();
						textures.addProperty("all", particle);
						json.add("textures", textures);
					}
					else
					{
						json.addProperty("parent", "block/cube");
						json.add("textures", builder.textures);
					}

					if (!builder.color.isEmpty())
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
							f.addProperty("texture", "#" + direction.getSerializedName());
							f.addProperty("cullface", direction.getSerializedName());
							f.addProperty("tintindex", 0);
							faces.add(direction.getSerializedName(), f);
						}

						cube.add("faces", faces);

						JsonArray elements = new JsonArray();
						elements.add(cube);
						json.add("elements", elements);
					}
				}

				return true;
			}
			else
			{
				FluidBuilder fluidBuilder = KubeJSObjects.FLUIDS.get(new ResourceLocation(namespace, path.substring(13)));

				if (fluidBuilder != null)
				{
					JsonObject textures = new JsonObject();
					textures.addProperty("particle", fluidBuilder.stillTexture);
					json.add("textures", textures);
					return true;
				}
			}
		}
		else if (path.startsWith("blockstates/"))
		{
			ResourceLocation id = new ResourceLocation(namespace, path.substring(12));
			BlockBuilder builder = KubeJSObjects.BLOCKS.get(id);

			if (builder != null)
			{
				if (real)
				{
					JsonObject variants = new JsonObject();
					JsonObject model = new JsonObject();
					model.addProperty("model", builder.model);
					variants.add("", model);
					json.add("variants", variants);
				}

				return true;
			}
			else
			{
				FluidBuilder fluidBuilder = KubeJSObjects.FLUIDS.get(id);

				if (fluidBuilder != null)
				{
					JsonObject variants = new JsonObject();
					JsonObject model = new JsonObject();
					model.addProperty("model", namespace + ":block/" + fluidBuilder.id.getPath());
					variants.add("", model);
					json.add("variants", variants);
					return true;
				}
			}
		}

		return false;
	}

	private boolean areAllTexturesEqual(JsonObject tex, String t)
	{
		for (Direction direction : Direction.values())
		{
			if (!tex.get(direction.getSerializedName()).getAsString().equals(t))
			{
				return false;
			}
		}

		return true;
	}

	private boolean generateServerJsonFile(String namespace, String path, JsonObject json, boolean real)
	{
		if (path.startsWith("loot_tables/blocks/"))
		{
			String blockId = path.substring(19);
			BlockBuilder builder = KubeJSObjects.BLOCKS.get(new ResourceLocation(namespace, blockId));

			if (builder != null)
			{
				if (real)
				{
					json.addProperty("type", "minecraft:block");
					JsonArray pools = new JsonArray();
					JsonObject pool = new JsonObject();
					pool.addProperty("rolls", 1);
					JsonArray entries = new JsonArray();
					JsonObject entry = new JsonObject();
					entry.addProperty("type", "minecraft:item");
					entry.addProperty("name", builder.id.toString());
					entries.add(entry);
					pool.add("entries", entries);
					JsonArray conditions = new JsonArray();
					JsonObject condition = new JsonObject();
					condition.addProperty("condition", "minecraft:survives_explosion");
					conditions.add(condition);
					pool.add("conditions", conditions);
					pools.add(pool);
					json.add("pools", pools);
				}

				return true;
			}
		}

		return false;
	}

	@Override
	public Collection<ResourceLocation> getResources(PackType type, String namespace, String path, int maxDepth, Predicate<String> filter)
	{
		if (type != packType)
		{
			return Collections.emptySet();
		}

		List<ResourceLocation> list = Lists.newArrayList();

		if (type == PackType.CLIENT_RESOURCES)
		{
			if (path.equals("lang"))
			{
				list.add(new ResourceLocation(KubeJS.MOD_ID, "lang/en_us.json"));
			}
		}
		else
		{
			if (path.equals("loot_tables"))
			{
				for (ResourceLocation id : KubeJSObjects.BLOCKS.keySet())
				{
					list.add(new ResourceLocation(id.getNamespace(), "loot_tables/blocks/" + id.getPath() + ".json"));
				}
			}
		}

		UtilsJS.tryIO(() ->
		{
			Path root = KubeJSPaths.get(type).toAbsolutePath();

			if (Files.exists(root) && Files.isDirectory(root))
			{
				Path inputPath = root.getFileSystem().getPath(path);

				Files.walk(root)
						.map(p -> root.relativize(p.toAbsolutePath()))
						.filter(p -> p.getNameCount() > 1 && p.getNameCount() - 1 <= maxDepth)
						.filter(p -> !p.toString().endsWith(".mcmeta"))
						.filter(p -> p.subpath(1, p.getNameCount()).startsWith(inputPath))
						.filter(p -> filter.test(p.getFileName().toString()))
						.map(p -> new ResourceLocation(p.getName(0).toString(), Joiner.on('/').join(p.subpath(1, Math.min(maxDepth, p.getNameCount())))))
						.forEach(list::add);
			}
		});

		return list;
	}

	@Override
	public Set<String> getNamespaces(PackType type)
	{
		if (type != packType)
		{
			return Collections.emptySet();
		}

		HashSet<String> namespaces = new HashSet<>();
		namespaces.add(KubeJS.MOD_ID);

		for (BuilderBase builder : KubeJSObjects.ALL)
		{
			namespaces.add(builder.id.getNamespace());
		}

		UtilsJS.tryIO(() ->
		{
			Path root = KubeJSPaths.get(type).toAbsolutePath();

			if (Files.exists(root) && Files.isDirectory(root))
			{
				Files.walk(root, 1)
						.map(path -> root.relativize(path.toAbsolutePath()))
						.filter(path -> path.getNameCount() > 0)
						.map(p -> p.toString().replaceAll("/$", ""))
						.filter(s -> !s.isEmpty())
						.forEach(namespaces::add);
			}
		});

		return namespaces;
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
		return "KubeJS Resource Pack [" + packType.getDirectory() + "]";
	}

	@Override
	public void close()
	{
	}
}
