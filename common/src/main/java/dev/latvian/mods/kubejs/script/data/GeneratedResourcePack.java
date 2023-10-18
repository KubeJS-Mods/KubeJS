package dev.latvian.mods.kubejs.script.data;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.util.Lazy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.ResourcePackFileNotFoundException;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public abstract class GeneratedResourcePack implements ExportablePackResources {
	private final PackType packType;
	private Map<ResourceLocation, GeneratedData> generated;
	private Set<String> generatedNamespaces;

	public GeneratedResourcePack(PackType t) {
		packType = t;
	}

	private static String getFullPath(PackType type, ResourceLocation location) {
		return String.format("%s/%s/%s", type.getDirectory(), location.getNamespace(), location.getPath());
	}

	@Override
	public InputStream getRootResource(String fileName) throws IOException {
		return switch (fileName) {
			case PACK_META -> GeneratedData.PACK_META.get();
			case "pack.png" -> GeneratedData.PACK_ICON.get();
			default -> throw new ResourcePackFileNotFoundException(KubeJSPaths.DIRECTORY.toFile(), fileName);
		};
	}

	public Map<ResourceLocation, GeneratedData> getGenerated() {
		if (generated == null) {
			generated = new HashMap<>();
			generate(generated);

			boolean debug = DevProperties.get().logGeneratedData || DevProperties.get().debugInfo;

			try {
				var root = KubeJSPaths.get(packType);

				for (var dir : Files.list(root).filter(Files::isDirectory).toList()) {
					var ns = dir.getFileName().toString();

					if (debug) {
						KubeJS.LOGGER.info("# Walking namespace '" + ns + "'");
					}

					for (var path : Files.walk(dir).filter(Files::isRegularFile).filter(Files::isReadable).toList()) {
						var pathStr = dir.relativize(path).toString().replace('\\', '/').toLowerCase();
						int sindex = pathStr.lastIndexOf('/');
						var fileName = sindex == -1 ? pathStr : pathStr.substring(sindex + 1);

						if (fileName.endsWith(".zip") || fileName.equals(".ds_store") || fileName.equals("thumbs.db") || fileName.equals("desktop.ini") || Files.isHidden(path)) {
							continue;
						}

						var data = new GeneratedData(new ResourceLocation(ns, pathStr), Lazy.of(() -> {
							try {
								return Files.readAllBytes(path);
							} catch (Exception ex) {
								ex.printStackTrace();
								return new byte[0];
							}
						}));

						if (debug) {
							KubeJS.LOGGER.info("- File found: '" + data.id() + "' (" + data.data().get().length + " bytes)");
						}

						generated.put(data.id(), data);
					}
				}
			} catch (Exception ex) {
				KubeJS.LOGGER.error("Failed to load files from kubejs/" + packType.getDirectory(), ex);
			}

			generated.put(GeneratedData.INTERNAL_RELOAD.id(), GeneratedData.INTERNAL_RELOAD);

			generated = Map.copyOf(generated);

			if (debug) {
				KubeJS.LOGGER.info("Generated " + packType + " data (" + generated.size() + " files)");
			}
		}

		return generated;
	}

	@Override
	public InputStream getResource(PackType type, ResourceLocation location) throws IOException {
		var r = type == packType ? getGenerated().get(location) : null;

		if (r == GeneratedData.INTERNAL_RELOAD) {
			close();
		}

		if (r != null) {
			return r.get();
		}

		throw new ResourcePackFileNotFoundException(KubeJSPaths.DIRECTORY.toFile(), getFullPath(type, location));
	}

	@Override
	public boolean hasResource(PackType type, ResourceLocation location) {
		return type == packType && getGenerated().get(location) != null;
	}

	public void generate(Map<ResourceLocation, GeneratedData> map) {
	}

	@Override
	public Collection<ResourceLocation> getResources(PackType type, String namespace, String path, Predicate<ResourceLocation> filter) {
		if (type != packType) {
			return Collections.emptySet();
		}

		var list = new ArrayList<ResourceLocation>();

		if (!path.endsWith("/")) {
			path = path + "/";
		}

		for (var r : getGenerated().entrySet()) {
			if (r.getKey().getNamespace().equals(namespace) && r.getKey().getPath().startsWith(path)) {
				if (filter.test(r.getKey())) {
					list.add(r.getKey());
				}
			}
		}

		return list;
	}

	@Override
	public Set<String> getNamespaces(PackType type) {
		if (type == packType) {
			if (generatedNamespaces == null) {
				generatedNamespaces = new HashSet<>();

				for (var s : getGenerated().entrySet()) {
					generatedNamespaces.add(s.getKey().getNamespace());
				}
			}

			return generatedNamespaces;
		}

		return Collections.emptySet();
	}

	@Nullable
	@Override
	public <T> T getMetadataSection(MetadataSectionSerializer<T> serializer) throws IOException {
		try (var in = this.getRootResource(PACK_META)) {
			return AbstractPackResources.getMetadataFromStream(serializer, in);
		}
	}

	@Override
	public String getName() {
		return "KubeJS Resource Pack [" + packType.getDirectory() + "]";
	}

	@Override
	public void close() {
		generated = null;
		generatedNamespaces = null;
	}

	@Override
	public void export(Path root) throws IOException {
		for (var file : getGenerated().entrySet()) {
			var path = root.resolve(packType.getDirectory() + "/" + file.getKey().getNamespace() + "/" + file.getKey().getPath());
			Files.createDirectories(path.getParent());
			Files.write(path, file.getValue().data().get());
		}

		Files.write(root.resolve(PACK_META), GeneratedData.PACK_META.data().get());
		Files.write(root.resolve("pack.png"), GeneratedData.PACK_ICON.data().get());
	}
}
