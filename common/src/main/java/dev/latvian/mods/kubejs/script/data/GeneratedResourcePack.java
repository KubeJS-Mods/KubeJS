package dev.latvian.mods.kubejs.script.data;

import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class GeneratedResourcePack implements PackResources {
	private final PackType packType;
	private Map<ResourceLocation, GeneratedData> generated;

	public GeneratedResourcePack(PackType t) {
		packType = t;
	}

	@Override
	@Nullable
	public IoSupplier<InputStream> getRootResource(String... fileName) {
		var joined = String.join("/", fileName);

		if (joined.equals("pack.png")) {
			return IoSupplier.create(KubeJS.thisMod.findResource("kubejs_logo.png").get());
		}

		/*return () -> {
			throw new FileNotFoundException(KubeJSPaths.DIRECTORY.resolve(joined).toString());
		};*/
		return null;
	}

	public Map<ResourceLocation, GeneratedData> getGenerated() {
		if (generated == null) {
			var map = new HashMap<ResourceLocation, byte[]>();
			generate(map);
			generated = map.entrySet().stream().collect(Collectors.toMap(GeneratedData.KEY, GeneratedData.VALUE));
		}

		return generated;
	}

	@Override
	@Nullable
	public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
		return type == packType ? getGenerated().get(location) : null;
	}

	public void generate(Map<ResourceLocation, byte[]> map) {
	}

	@Override
	public void listResources(PackType type, String namespace, String path, ResourceOutput visitor) {
		if (type != packType) {
			return;
		}

		if (packType == PackType.CLIENT_RESOURCES) {
			if (path.equals("lang")) {
				var id = new ResourceLocation(KubeJS.MOD_ID, "lang/en_us.json");
				visitor.accept(id, getResource(packType, id));
			}
		}

		/*
		var resources = new ArrayList<ResourceLocation>();

		for (var builder : RegistryInfo.ALL_BUILDERS) {
			builder.addResourcePackLocations(path, resources, packType);
		}

		if (Platform.isDevelopmentEnvironment()) {
			KubeJS.LOGGER.info("Resources " + type + " " + namespace + " " + path + "\n - " + resources);
		}

		for (var id : resources) {
			var r = getResource(packType, id);

			if (r != null) {
				visitor.accept(id, r);
			} else {
				KubeJS.LOGGER.warn("A builder stated that resource " + id + " exists, but it doesn't");
			}
		}
		 */

		for (var r : getGenerated().entrySet()) {
			if (r.getKey().getPath().startsWith(path)) {
				visitor.accept(r.getKey(), r.getValue());
			}
		}
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
	public @NotNull String packId() {
		return "KubeJS Resource Pack [" + packType.getDirectory() + "]";
	}

	@Override
	public void close() {
		generated = null;
	}
}
