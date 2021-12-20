package dev.latvian.mods.kubejs.script.data;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSObjects;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.util.UtilsJS;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public abstract class KubeJSResourcePack implements PackResources {
	private final PackType packType;
	private Map<ResourceLocation, JsonElement> cachedResources;

	public KubeJSResourcePack(PackType t) {
		packType = t;
		Objects.requireNonNull(KubeJS.instance, "KubeJS has not been initialized, this won't happen unless some OTHER mod failed to load first! Check your latest.log!");
	}

	private static String getFullPath(PackType type, ResourceLocation location) {
		return String.format("%s/%s/%s", type.getDirectory(), location.getNamespace(), location.getPath());
	}

	@Override
	public InputStream getRootResource(String fileName) throws IOException {
		if (fileName.equals("pack.png")) {
			return KubeJSResourcePack.class.getResourceAsStream("/kubejs_logo.png");
		}

		throw new ResourcePackFileNotFoundException(KubeJSPaths.DIRECTORY.toFile(), fileName);
	}

	@Override
	public InputStream getResource(PackType type, ResourceLocation location) throws IOException {
		var resourcePath = getFullPath(type, location);

		if (type != packType) {
			throw new IllegalStateException(packType.getDirectory() + " KubeJS pack can't load " + resourcePath + "!");
		}

		var file = KubeJSPaths.DIRECTORY.resolve(resourcePath);

		if (Files.exists(file)) {
			return Files.newInputStream(file);
		} else {
			if (location.getPath().endsWith(".json")) {
				var json = getCachedResources().get(location);

				if (json != null) {
					return new ByteArrayInputStream(json.toString().getBytes(StandardCharsets.UTF_8));
				}
			}
		}

		throw new ResourcePackFileNotFoundException(KubeJSPaths.DIRECTORY.toFile(), resourcePath);
	}

	@Override
	public boolean hasResource(PackType type, ResourceLocation location) {
		if (location.getPath().endsWith(".json")) {
			var json = getCachedResources().get(location);

			if (json != null) {
				return true;
			}
		}

		return type == packType && Files.exists(KubeJSPaths.DIRECTORY.resolve(getFullPath(type, location)));
	}

	public Map<ResourceLocation, JsonElement> getCachedResources() {
		if (cachedResources == null) {
			Map<ResourceLocation, JsonElement> map = new HashMap<>();
			generateJsonFiles(map);

			cachedResources = new HashMap<>();

			for (var entry : map.entrySet()) {
				cachedResources.put(new ResourceLocation(entry.getKey().getNamespace(), entry.getKey().getPath() + ".json"), entry.getValue());
			}
		}

		return cachedResources;
	}

	public void generateJsonFiles(Map<ResourceLocation, JsonElement> map) {
	}

	@Override
	public Collection<ResourceLocation> getResources(PackType type, String namespace, String path, int maxDepth, Predicate<String> filter) {
		if (type != packType) {
			return Collections.emptySet();
		}

		List<ResourceLocation> list = Lists.newArrayList();

		if (type == PackType.CLIENT_RESOURCES) {
			if (path.equals("lang")) {
				list.add(new ResourceLocation(KubeJS.MOD_ID, "lang/en_us.json"));
			}
		} else {
			if (path.equals("loot_tables")) {
				for (var id : KubeJSObjects.BLOCKS.keySet()) {
					list.add(new ResourceLocation(id.getNamespace(), "loot_tables/blocks/" + id.getPath() + ".json"));
				}
			}
		}

		UtilsJS.tryIO(() ->
		{
			var root = KubeJSPaths.get(type).toAbsolutePath();

			if (Files.exists(root) && Files.isDirectory(root)) {
				var inputPath = root.getFileSystem().getPath(path);

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
	public Set<String> getNamespaces(PackType type) {
		if (type != packType) {
			return Collections.emptySet();
		}

		var namespaces = new HashSet<String>();
		namespaces.add("kubejs_generated");
		namespaces.add(KubeJS.MOD_ID);

		for (var builder : KubeJSObjects.ALL) {
			namespaces.add(builder.id.getNamespace());
		}

		UtilsJS.tryIO(() ->
		{
			var root = KubeJSPaths.get(type).toAbsolutePath();

			if (Files.exists(root) && Files.isDirectory(root)) {
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
	public <T> T getMetadataSection(MetadataSectionSerializer<T> serializer) {
		return null;
	}

	@Override
	public String getName() {
		return "KubeJS Resource Pack [" + packType.getDirectory() + "]";
	}

	@Override
	public void close() {
		cachedResources = null;
	}
}
