package dev.latvian.mods.kubejs.script.data;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class KubeFileResourcePack implements PackResources {
	public static final PackLocationInfo PACK_LOCATION_INFO = new PackLocationInfo(KubeJS.MOD_ID, Component.empty(), PackSource.BUILT_IN, Optional.empty());

	private static Stream<Path> tryWalk(Path path) {
		try {
			return Files.walk(path);
		} catch (Exception ignore) {
		}

		return Stream.empty();
	}

	public static void scanForInvalidFiles(String pathName, Path path) throws IOException {
		for (var p : Files.list(path).filter(Files::isDirectory).flatMap(KubeFileResourcePack::tryWalk).filter(Files::isRegularFile).filter(Files::isReadable).toList()) {
			try {
				var fileName = p.getFileName().toString();
				var fileNameLC = fileName.toLowerCase(Locale.ROOT);

				if (fileNameLC.endsWith(".zip") || fileNameLC.equals(".ds_store") || fileNameLC.equals("thumbs.db") || fileNameLC.equals("desktop.ini")) {
					continue;
				} else if (Files.isHidden(path)) {
					ConsoleJS.STARTUP.error("Invisible file found: " + pathName + path.relativize(p).toString().replace('\\', '/')).withExternalFile(p);
					continue;
				}

				var chars = fileName.toCharArray();

				for (char c : chars) {
					if (c >= 'A' && c <= 'Z') {
						ConsoleJS.STARTUP.error("Invalid file name: Uppercase '" + c + "' in " + pathName + path.relativize(p).toString().replace('\\', '/')).withExternalFile(p);
						break;
					} else if (c != '_' && c != '-' && (c < 'a' || c > 'z') && (c < '0' || c > '9') && c != '/' && c != '.') {
						ConsoleJS.STARTUP.error("Invalid file name: Invalid character '" + c + "' in " + pathName + path.relativize(p).toString().replace('\\', '/')).withExternalFile(p);
						break;
					}
				}
			} catch (Exception ex) {
				ConsoleJS.STARTUP.error("Invalid file name: " + pathName + path.relativize(p).toString().replace('\\', '/'), ex).withExternalFile(p);
			}
		}
	}

	public static int findBeforeModsIndex(List<PackResources> packs) {
		for (int i = 0; i < packs.size(); i++) {
			var pack = packs.get(i);

			if (pack instanceof VanillaPackResources) {
				return i + 1;
			}
		}

		return 1;
	}

	public static int findAfterModsIndex(List<PackResources> packs) {
		for (int i = packs.size() - 1; i >= 0; i--) {
			var pack = packs.get(i);

			if (pack instanceof FilePackResources) {
				return i + 1;
			}
		}

		return packs.size();
	}

	public static void scanAndLoad(Path path, List<PackResources> packs) {
		for (var file : Objects.requireNonNull(path.toFile().listFiles())) {
			var fileName = file.getName();

			if (file.isFile() && fileName.endsWith(".zip")) {
				var packName = new StringBuilder();

				for (var c : fileName.toCharArray()) {
					if (c == '_' || c == '.' || c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
						packName.append(c);
					}
				}

				long lastModified = 0L;

				if (file.exists()) {
					lastModified = file.lastModified();
				}

				packs.add(new FilePackResources(new PackLocationInfo(fileName, Component.literal(fileName), PackSource.BUILT_IN, Optional.of(new KnownPack(KubeJS.MOD_ID, "kubejs_file_" + packName.toString().toLowerCase(Locale.ROOT), lastModified <= 0L ? "1" : Long.toUnsignedString(lastModified)))), new FilePackResources.SharedZipFileAccess(file), ""));
			}
		}
	}

	private final PackType packType;
	private Map<ResourceLocation, GeneratedData> generated;
	private Set<String> generatedNamespaces;

	public KubeFileResourcePack(PackType t) {
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

			boolean debug = DevProperties.get().logGeneratedData;

			try {
				var root = KubeJSPaths.get(packType);

				for (var dir : Files.list(root).filter(Files::isDirectory).toList()) {
					var ns = dir.getFileName().toString();

					if (debug) {
						KubeJS.LOGGER.info("# Walking namespace '" + ns + "'");
					}

					for (var path : Files.walk(dir).filter(Files::isRegularFile).filter(Files::isReadable).toList()) {
						var pathStr = dir.relativize(path).toString().replace('\\', '/').toLowerCase(Locale.ROOT);
						int sindex = pathStr.lastIndexOf('/');
						var fileNameLC = sindex == -1 ? pathStr : pathStr.substring(sindex + 1);

						if (fileNameLC.endsWith(".zip") || fileNameLC.equals(".ds_store") || fileNameLC.equals("thumbs.db") || fileNameLC.equals("desktop.ini") || Files.isHidden(path)) {
							continue;
						}

						var data = new GeneratedData(ResourceLocation.fromNamespaceAndPath(ns, pathStr), () -> {
							try {
								return Files.readAllBytes(path);
							} catch (Exception ex) {
								ex.printStackTrace();
								return new byte[0];
							}
						});

						if (debug) {
							KubeJS.LOGGER.info("- File found: '" + data.id() + "' (" + data.data().get().length + " bytes)");
						}

						if (skipFile(data)) {
							if (debug) {
								KubeJS.LOGGER.info("- Skipping '" + data.id() + "'");
							}

							continue;
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

	protected boolean skipFile(GeneratedData data) {
		if (packType == PackType.CLIENT_RESOURCES) {
			return data.id().getPath().startsWith("lang/");
		} else {
			return false;
		}
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
		return "KubeJS File Resource Pack [" + packType.getDirectory() + "]";
	}

	@Override
	public void close() {
		generated = null;
		generatedNamespaces = null;
	}

	@Override
	public PackLocationInfo location() {
		return PACK_LOCATION_INFO;
	}

	@Override
	public String toString() {
		return packId();
	}

	/*
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
	*/
}
