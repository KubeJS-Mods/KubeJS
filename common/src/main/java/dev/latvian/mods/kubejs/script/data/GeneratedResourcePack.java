package dev.latvian.mods.kubejs.script.data;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

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

	@Override
	public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
		if (type != packType) {
			return null;
		}

		return () -> new ByteArrayInputStream(getGenerated().get(location));

		// throw new ResourcePackFileNotFoundException(KubeJSPaths.DIRECTORY.toFile(), getFullPath(type, location));
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

		var resources = new ArrayList<ResourceLocation>();
		for (var builder : RegistryInfo.ALL_BUILDERS) {
			builder.addResourcePackLocations(path, resources, packType);
		}

		for (var id : resources) {
			visitor.accept(id, getResource(packType, id));
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
