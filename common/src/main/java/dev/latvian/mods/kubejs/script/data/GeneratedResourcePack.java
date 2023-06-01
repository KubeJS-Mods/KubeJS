package dev.latvian.mods.kubejs.script.data;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.ResourcePackFileNotFoundException;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public abstract class GeneratedResourcePack implements PackResources {
	private final PackType packType;
	private Map<ResourceLocation, byte[]> generated;

	public GeneratedResourcePack(PackType t) {
		packType = t;
	}

	private static String getFullPath(PackType type, ResourceLocation location) {
		return String.format("%s/%s/%s", type.getDirectory(), location.getNamespace(), location.getPath());
	}

	@Override
	public InputStream getRootResource(String fileName) throws IOException {
		if (fileName.equals("pack.png")) {
			return Files.newInputStream(KubeJS.thisMod.findResource("kubejs_logo.png").get());
		}

		throw new ResourcePackFileNotFoundException(KubeJSPaths.DIRECTORY.toFile(), fileName);
	}

	@Override
	public InputStream getResource(PackType type, ResourceLocation location) throws IOException {
		var bytes = type == packType ? getGenerated().get(location) : null;

		if (bytes != null) {
			return new ByteArrayInputStream(bytes);
		}

		throw new ResourcePackFileNotFoundException(KubeJSPaths.DIRECTORY.toFile(), getFullPath(type, location));
	}

	@Override
	public boolean hasResource(PackType type, ResourceLocation location) {
		return type == packType && getGenerated().get(location) != null;
	}

	public Map<ResourceLocation, byte[]> getGenerated() {
		if (generated == null) {
			generated = new HashMap<>();
			generate(generated);
		}

		return generated;
	}

	public void generate(Map<ResourceLocation, byte[]> map) {
	}

	@Override
	public Collection<ResourceLocation> getResources(PackType type, String namespace, String path, Predicate<ResourceLocation> filter) {
		if (type != packType) {
			return Collections.emptySet();
		}

		var list = new ArrayList<ResourceLocation>();

		if (packType == PackType.CLIENT_RESOURCES) {
			if (path.equals("lang")) {
				list.add(new ResourceLocation(KubeJS.MOD_ID, "lang/en_us.json"));
			}
		}

		for (var builder : RegistryInfo.ALL_BUILDERS) {
			builder.addResourcePackLocations(path, list, packType);
		}

		return list;
	}

	@Override
	public Set<String> getNamespaces(PackType type0) {
		if (type0 != packType) {
			return Collections.emptySet();
		}

		var namespaces = new HashSet<String>();

		for (var key : getGenerated().entrySet()) {
			namespaces.add(key.getKey().getNamespace());
		}

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
		generated = null;
	}
}
