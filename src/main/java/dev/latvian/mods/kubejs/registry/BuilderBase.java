package dev.latvian.mods.kubejs.registry;

import dev.latvian.mods.kubejs.client.LangKubeEvent;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.generator.DataJsonGenerator;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

@ReturnsSelf
public abstract class BuilderBase<T> implements Supplier<T> {
	public final ResourceLocation id;
	protected T object;
	public String translationKey;
	public Component displayName;
	public boolean formattedDisplayName;
	public transient boolean dummyBuilder;
	public transient Set<ResourceLocation> defaultTags;

	public BuilderBase(ResourceLocation i) {
		id = i;
		object = null;
		translationKey = "";
		displayName = null;
		formattedDisplayName = false;
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

	public void createAdditionalObjects(AdditionalObjectRegistry registry) {
	}

	public String getTranslationKeyGroup() {
		return getRegistryType().languageKeyPrefix;
	}

	@Info("""
		Sets the translation key for this object, e.g. `block.minecraft.stone`.
		""")
	public BuilderBase<T> translationKey(String key) {
		translationKey = key;
		return this;
	}

	@Info("""
		Sets the display name for this object, e.g. `Stone`.

		This will be overridden by a lang file if it exists.
		""")
	public BuilderBase<T> displayName(Component name) {
		displayName = name;
		return this;
	}

	@Info("""
		Makes displayName() override language files.
		""")
	public BuilderBase<T> formattedDisplayName() {
		formattedDisplayName = true;
		return this;
	}

	@Info("""
		Combined method of formattedDisplayName().displayName(name).
		""")
	public BuilderBase<T> formattedDisplayName(Component name) {
		return formattedDisplayName().displayName(name);
	}

	@Info("""
		Adds a tag to this object, e.g. `minecraft:stone`.
		""")
	public BuilderBase<T> tag(ResourceLocation[] tag) {
		defaultTags.addAll(Arrays.asList(tag));
		getRegistryType().hasDefaultTags = true;
		return this;
	}

	public ResourceLocation newID(String pre, String post) {
		if (pre.isEmpty() && post.isEmpty()) {
			return id;
		}

		return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), pre + id.getPath() + post);
	}

	public void generateDataJsons(DataJsonGenerator generator) {
	}

	public void generateAssetJsons(AssetJsonGenerator generator) {
	}

	public String getBuilderTranslationKey() {
		if (translationKey.isEmpty()) {
			return Util.makeDescriptionId(getTranslationKeyGroup(), id);
		}

		return translationKey;
	}

	public void generateLang(LangKubeEvent lang) {
		if (displayName != null) {
			lang.add(id.getNamespace(), getBuilderTranslationKey(), displayName.getString());
		} else {
			lang.add(id.getNamespace(), getBuilderTranslationKey(), UtilsJS.snakeCaseToTitleCase(id.getPath()));
		}
	}

	protected T createTransformedObject() {
		object = transformObject(createObject());
		return object;
	}

	@Override
	public String toString() {
		var n = getClass().getName();
		int i = n.lastIndexOf('.');

		if (i != -1) {
			n = n.substring(i + 1);
		}

		return n + "[" + id + "]";
	}
}
