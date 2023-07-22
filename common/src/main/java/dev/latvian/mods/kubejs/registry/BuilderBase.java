package dev.latvian.mods.kubejs.registry;

import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.generator.DataJsonGenerator;
import dev.latvian.mods.kubejs.typings.JsInfo;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public abstract class BuilderBase<T> implements Supplier<T> {
	public final ResourceLocation id;
	protected T object;
	public String translationKey;
	public String displayName;
	public transient boolean dummyBuilder;
	public transient Set<ResourceLocation> defaultTags;

	public BuilderBase(ResourceLocation i) {
		id = i;
		object = null;
		translationKey = "";
		displayName = "";
		dummyBuilder = false;
		defaultTags = new HashSet<>();
	}

	public abstract RegistryInfo getRegistryType();

	public abstract T createObject();

	public T transformObject(T obj) {
		return obj;
	}

	@Override
	public final T get() {
		try {
			return object;
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

	@JsInfo("""
			Sets the translation key for this object, e.g. `block.minecraft.stone`.
			""")
	public BuilderBase<T> translationKey(String key) {
		translationKey = key;
		return this;
	}

	@JsInfo("""
			Sets the display name for this object, e.g. `Stone`.

			This will be overridden by a lang file if it exists.
			""")
	public BuilderBase<T> displayName(String name) {
		displayName = name;
		return this;
	}

	@JsInfo("""
			Adds a tag to this object, e.g. `minecraft:stone`.
			""")
	public BuilderBase<T> tag(ResourceLocation tag) {
		defaultTags.add(tag);
		getRegistryType().hasDefaultTags = true;
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
		var tkey = translationKey;

		if (tkey.isEmpty()) {
			tkey = getTranslationKeyGroup() + '.' + id.getNamespace() + '.' + id.getPath();
		}

		var dname = displayName;

		if (dname.isEmpty()) {
			dname = UtilsJS.snakeCaseToTitleCase(id.getPath());
		}

		lang.put(tkey, dname);
	}

	public void addResourcePackLocations(String path, List<ResourceLocation> list, PackType packType) {
	}

	protected T createTransformedObject() {
		object = transformObject(createObject());
		return object;
	}
}
