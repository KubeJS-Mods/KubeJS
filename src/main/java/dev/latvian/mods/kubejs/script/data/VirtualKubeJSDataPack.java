package dev.latvian.mods.kubejs.script.data;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.TextIcons;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.world.flag.FeatureFlagSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class VirtualKubeJSDataPack extends AbstractPackResources implements ExportablePackResources {
	public static final VirtualKubeJSDataPack HIGH = new VirtualKubeJSDataPack(true);
	public static final VirtualKubeJSDataPack LOW = new VirtualKubeJSDataPack(false);

	public final boolean high;
	public final Component name;
	public final Pack pack;
	private final Map<ResourceLocation, String> locationToData;
	private final Map<String, String> pathToData;
	private final Set<String> namespaces;

	private VirtualKubeJSDataPack(boolean high) {
		super(GeneratedResourcePack.PACK_LOCATION_INFO);
		this.high = high;
		this.name = Component.empty().append(TextIcons.NAME).append(high ? " (High)" : " (Low)");

		var id = high ? "kubejs_virtual_high" : "kubejs_virtual_low";
		this.pack = new Pack(
			new PackLocationInfo(id, name, PackSource.BUILT_IN, Optional.of(new KnownPack(KubeJS.MOD_ID, id, "1"))),
			// BuiltInPackSource.fromName((path) -> new PathPackResources(path, resourcePath)),
			BuiltInPackSource.fixedResources(this),
			new Pack.Metadata(name, PackCompatibility.COMPATIBLE, FeatureFlagSet.of(), List.of(), true),
			new PackSelectionConfig(true, high ? Pack.Position.BOTTOM : Pack.Position.TOP, true)
		);

		this.locationToData = new HashMap<>();
		this.pathToData = new HashMap<>();
		this.namespaces = new HashSet<>();
	}

	public void reset() {
		locationToData.clear();
		pathToData.clear();
		namespaces.clear();
	}

	public void addData(ResourceLocation id, String data) {
		locationToData.put(id, data);
		pathToData.put("data/" + id.getNamespace() + "/" + id.getPath(), data);
		namespaces.add(id.getNamespace());

		if (DevProperties.get().dataPackOutput) {
			ConsoleJS.SERVER.info("Registered virtual file [" + (high ? "high" : "low") + " priority] '" + id + "': " + data);
		}
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
		if (type != PackType.SERVER_DATA) {
			return null;
		}

		var s = locationToData.get(location);

		if (s != null) {
			if (DevProperties.get().dataPackOutput) {
				ConsoleJS.SERVER.info("Served virtual file [" + (high ? "high" : "low") + " priority] '" + location + "': " + s);
			}

			return () -> new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
		}

		return null;
	}

	@Override
	public void listResources(PackType packType, String namespace, String path, ResourceOutput visitor) {
		if (!path.endsWith("/")) {
			path = path + "/";
		}

		for (ResourceLocation r : locationToData.keySet()) {
			if (!r.getPath().endsWith(".mcmeta")) {
				if (r.getNamespace().equals(namespace) && r.getPath().startsWith(path)) {
					visitor.accept(r, getResource(packType, r));
				}
			}
		}
	}

	@Override
	public Set<String> getNamespaces(PackType type) {
		return new HashSet<>(namespaces);
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
		return "KubeJS Virtual Data Pack [" + (high ? "high" : "low") + " priority]";
	}

	@Override
	public void export(Path root) throws IOException {
		for (var file : pathToData.entrySet()) {
			var path = root.resolve(file.getKey());
			Files.createDirectories(path.getParent());
			Files.writeString(path, file.getValue(), StandardCharsets.UTF_8);
		}
	}

	@Override
	public void close() {
	}

	public boolean hasNamespace(String key) {
		return namespaces.contains(key);
	}
}
