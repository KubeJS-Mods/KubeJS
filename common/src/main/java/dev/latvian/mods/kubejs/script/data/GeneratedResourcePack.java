package dev.latvian.mods.kubejs.script.data;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.util.Lazy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class GeneratedResourcePack implements ExportablePackResources {
	private final PackType packType;
	private Map<ResourceLocation, GeneratedData> generated;
	private Set<String> generatedNamespaces;

	public GeneratedResourcePack(PackType t) {
		packType = t;
	}

	@Nullable
	@Override
	public GeneratedData getRootResource(String... path) {
		return switch (path.length == 1 ? path[0] : "") {
			case PACK_META -> GeneratedData.PACK_META;
			case "pack.png" -> GeneratedData.PACK_ICON;
			default -> null;
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
						var pathStr = dir.relativize(path).toString().replace('\\', '/');

						if (pathStr.endsWith(".zip")) {
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
	@Nullable
	public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
		var r = type == packType ? getGenerated().get(location) : null;

		if (r == GeneratedData.INTERNAL_RELOAD) {
			close();
		}

		return r;
	}

	public void generate(Map<ResourceLocation, GeneratedData> map) {
	}

	@Override
	public void listResources(PackType type, String namespace, String path, ResourceOutput visitor) {
		if (type == packType) {
			if (!path.endsWith("/")) {
				path = path + "/";
			}

			for (var r : getGenerated().entrySet()) {
				if (r.getKey().getNamespace().equals(namespace) && r.getKey().getPath().startsWith(path)) {
					visitor.accept(r.getKey(), r.getValue());
				}
			}
		}
	}

	@Override
	@NotNull
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
		var inputSupplier = this.getRootResource(PACK_META);

		if (inputSupplier != null) {
			try (var input = inputSupplier.get()) {
				return AbstractPackResources.getMetadataFromStream(serializer, input);
			}
		}

		return null;
	}

	@Override
	@NotNull
	public String packId() {
		return "KubeJS Resource Pack [" + packType.getDirectory() + "]";
	}

	@Override
	public void close() {
		generated = null;
		generatedNamespaces = null;
	}

	@Override
	public boolean isBuiltin() {
		return true;
	}

	@Override
	public void export(FileSystem fs) throws IOException {
		for (var file : getGenerated().entrySet()) {
			var path = fs.getPath(packType.getDirectory() + "/" + file.getKey().getNamespace() + "/" + file.getKey().getPath());
			Files.createDirectories(path.getParent());
			Files.write(path, file.getValue().data().get());
		}

		Files.write(fs.getPath(PACK_META), GeneratedData.PACK_META.data().get());
		Files.write(fs.getPath("pack.png"), GeneratedData.PACK_ICON.data().get());
	}
}
