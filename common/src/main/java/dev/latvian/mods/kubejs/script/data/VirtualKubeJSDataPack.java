package dev.latvian.mods.kubejs.script.data;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.util.ConsoleJS;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class VirtualKubeJSDataPack extends AbstractPackResources implements ExportablePackResources {
	public final boolean high;
	private final Map<ResourceLocation, String> locationToData;
	private final Map<String, String> pathToData;
	private final Set<String> namespaces;

	public VirtualKubeJSDataPack(boolean h) {
		super(new File("dummy"));
		high = h;
		locationToData = new HashMap<>();
		pathToData = new HashMap<>();
		namespaces = new HashSet<>();
	}

	public void addData(ResourceLocation id, String data) {
		locationToData.put(id, data);
		pathToData.put("data/" + id.getNamespace() + "/" + id.getPath(), data);
		namespaces.add(id.getNamespace());

		if (DevProperties.get().dataPackOutput) {
			ConsoleJS.SERVER.info("Registered virtual file [" + (high ? "high" : "low") + " priority] '" + id + "': " + data);
		}
	}

	@Override
	public InputStream getResource(String path) throws IOException {
		var s = pathToData.get(path);

		if (s != null) {
			if (DevProperties.get().dataPackOutput) {
				ConsoleJS.SERVER.info("Served virtual file [" + (high ? "high" : "low") + " priority] '" + path + "': " + s);
			}

			return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
		}

		throw new FileNotFoundException(path);
	}

	@Override
	public InputStream getResource(PackType type, ResourceLocation location) throws IOException {
		var s = locationToData.get(location);

		if (s != null) {
			if (DevProperties.get().dataPackOutput) {
				ConsoleJS.SERVER.info("Served virtual file [" + (high ? "high" : "low") + " priority] '" + location + "': " + s);
			}

			return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
		}

		throw new FileNotFoundException(location.toString());
	}

	@Override
	public boolean hasResource(String path) {
		return pathToData.containsKey(path);
	}

	@Override
	public boolean hasResource(PackType type, ResourceLocation location) {
		return type == PackType.SERVER_DATA && locationToData.containsKey(location);
	}

	@Override
	public Collection<ResourceLocation> getResources(PackType type, String namespace, String path, Predicate<ResourceLocation> filter) {
		return locationToData.keySet()
			.stream()
			.filter(r -> !r.getPath().endsWith(".mcmeta"))
			.filter(r -> r.getNamespace().equals(namespace) && r.getPath().startsWith(path))
			.filter(filter)
			.toList();
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
	public String getName() {
		return this.toString();
	}

	@Override
	public String toString() {
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
