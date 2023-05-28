package dev.latvian.mods.kubejs.registry;

import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.generator.DataJsonGenerator;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class BuilderBase {
	public final ResourceLocation id;
	protected Object object;
	public String translationKey;
	public String displayName;
	public transient boolean dummyBuilder;
	public transient Set<ResourceLocation> defaultTags;

	public BuilderBase(ResourceLocation i) {
		id = i;
		object = null;
		translationKey = getTranslationKeyGroup() + "." + id.getNamespace() + "." + id.getPath();
		displayName = Arrays.stream(id.getPath().split("_")).map(UtilsJS::toTitleCase).collect(Collectors.joining(" "));
		dummyBuilder = false;
		defaultTags = new HashSet<>();
	}

	public abstract RegistryInfo getRegistryType();

	public abstract Object createObject();

	public Object transformObject(Object obj) {
		return obj;
	}

	@SuppressWarnings("unchecked")
	public <T> T getObject() {
		try {
			return (T) object;
		} catch (Exception ex) {
			if (dummyBuilder) {
				throw new RuntimeException("Object '" + id + "' of registry '" + getRegistryType().key.location() + "' is from a dummy builder and doesn't have a value!");
			} else {
				throw new RuntimeException("Object '" + id + "' of registry '" + getRegistryType().key.location() + "' hasn't been registered yet!", ex);
			}
		}
	}

	public void createAdditionalObjects() {
	}

	public String getTranslationKeyGroup() {
		return getRegistryType().key.location().getPath();
	}

	public BuilderBase translationKey(String key) {
		translationKey = key;
		return this;
	}

	public BuilderBase displayName(String name) {
		displayName = name;
		return this;
	}

	public BuilderBase tag(ResourceLocation tag) {
		defaultTags.add(tag);
		return this;
	}

	public ResourceLocation newID(String pre, String post) {
		if (pre.isEmpty() && post.isEmpty()) {
			return id;
		}

		return new ResourceLocation(id.getNamespace() + ':' + pre + id.getPath() + post);
	}

	public void generateDataJsons(DataJsonGenerator generator) {
	}

	public void generateAssetJsons(AssetJsonGenerator generator) {
	}

	public void generateLang(Map<String, String> lang) {
		lang.put(translationKey, displayName);
	}

	public void addResourcePackLocations(String path, List<ResourceLocation> list, PackType packType) {
	}

	protected Object createTransformedObject() {
		object = transformObject(createObject());
		return object;
	}
}
