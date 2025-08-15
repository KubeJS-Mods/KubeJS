package dev.latvian.mods.kubejs.script.data;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.generator.KubeResourceGenerator;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.TextIcons;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class VirtualResourcePack extends AbstractPackResources implements KubeResourceGenerator, ExportablePackResources {
	public final ScriptType scriptType;
	public final PackType packType;
	public final GeneratedDataStage stage;
	public final Supplier<RegistryAccessContainer> registries;
	public final String info;
	public final Component component;
	private final Map<ResourceLocation, GeneratedData> locationToData;
	private final Map<String, GeneratedData> pathToData;
	private final Set<String> namespaces;
	private final Map<DataMapType<?, ?>, Map<ResourceLocation, Object>> dataMaps;

	public VirtualResourcePack(ScriptType scriptType, PackType packType, GeneratedDataStage stage, Supplier<RegistryAccessContainer> registries) {
		super(KubeFileResourcePack.PACK_LOCATION_INFO);
		this.scriptType = scriptType;
		this.packType = packType;
		this.stage = stage;
		this.registries = registries;
		this.info = stage.displayName + ", " + packType.getDirectory();
		this.component = Component.empty().append(TextIcons.NAME).append(" (" + info + ", )");

		this.locationToData = new HashMap<>();
		this.pathToData = new HashMap<>();
		this.namespaces = new HashSet<>();
		this.dataMaps = new HashMap<>();
	}

	public void reset() {
		locationToData.clear();
		pathToData.clear();
		namespaces.clear();
		dataMaps.clear();
	}

	@Override
	public RegistryAccessContainer getRegistries() {
		return registries.get();
	}

	@Override
	public void add(GeneratedData data) {
		locationToData.put(data.id(), data);
		pathToData.put(packType.getDirectory() + "/" + data.id().getNamespace() + "/" + data.id().getPath(), data);
		namespaces.add(data.id().getNamespace());

		if (DevProperties.get().virtualPackOutput) {
			scriptType.console.info("Registered virtual file [" + info + "] '" + data.id() + "': " + data);
		}
	}

	@Override
	public <R, T> void dataMap(DataMapType<R, T> type, Consumer<BiConsumer<ResourceLocation, T>> consumer) {
		var map = dataMaps.computeIfAbsent(type, k -> new HashMap<>());
		consumer.accept(map::put);
	}

	@Override
	public void flush() {
		var jsonOps = getRegistries().json();

		for (var typeEntry : dataMaps.entrySet()) {
			var type = typeEntry.getKey();
			var json = new JsonObject();
			var valuesJson = new JsonObject();

			for (var entry : typeEntry.getValue().entrySet()) {
				var data = entry.getValue();
				var result = type.codec().encodeStart(jsonOps, Cast.to(data));

				if (result.isSuccess()) {
					valuesJson.add(entry.getKey().toString(), result.getOrThrow());
				} else {
					throw new RuntimeException("Failed to encode data for " + type.registryKey().location() + " / " + type.id() + " / " + entry.getKey() + ": " + result.error().get().message());
				}
			}

			json.add("values", valuesJson);

			add(GeneratedData.json(type.id().withPath("data_maps/" + ID.resourcePath(type.registryKey().location()) + "/" + type.id().getPath() + ".json"), () -> json));
		}

		dataMaps.clear();
	}

	@Override
	@Nullable
	public GeneratedData getGenerated(ResourceLocation id) {
		return locationToData.get(id);
	}

	@Nullable
	@Override
	public IoSupplier<InputStream> getRootResource(String... path) {
		return switch (path.length == 1 ? path[0] : "") {
			case PACK_META -> GeneratedData.PACK_META;
			case "pack.png" -> GeneratedData.PACK_ICON;
			default -> null;
		};
	}

	@Override
	@Nullable
	public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
		if (type != packType) {
			return null;
		}

		var s = locationToData.get(location);

		if (s != null) {
			if (DevProperties.get().virtualPackOutput) {
				scriptType.console.info("Served virtual file [" + info + "] '" + location + "': " + s);
			}

			return s;
		}

		return null;
	}

	@Override
	public void listResources(PackType packType, String namespace, String path, ResourceOutput visitor) {
		if (!path.endsWith("/")) {
			path = path + "/";
		}

		for (ResourceLocation r : locationToData.keySet()) {
			if (r.getNamespace().equals(namespace) && r.getPath().startsWith(path)) {
				visitor.accept(r, getResource(packType, r));
			}
		}
	}

	@Override
	public Set<String> getNamespaces(PackType type) {
		return Set.copyOf(namespaces);
	}

	@Nullable
	@Override
	public <T> T getMetadataSection(MetadataSectionSerializer<T> serializer) {
		return null;
	}

	@Override
	public String toString() {
		return packId();
	}

	@Override
	public @NotNull String packId() {
		return "KubeJS Virtual Resource Pack [" + info + "]";
	}

	@Override
	public String exportPath() {
		return packType.getDirectory() + '/' + stage.name;
	}

	@Override
	public void export(Path root) throws IOException {
		for (var file : pathToData.entrySet()) {
			var path = root.resolve(file.getKey());
			var parent = path.getParent();

			if (Files.notExists(parent)) {
				Files.createDirectories(parent);
			}

			Files.write(path, file.getValue().data().get());
		}
	}

	@Override
	public void close() {
		if (!FMLLoader.isProduction()) {
			KubeJS.LOGGER.info("Closed " + packId());
		}
	}
}
