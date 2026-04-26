package dev.latvian.mods.kubejs.registry;

import dev.latvian.mods.kubejs.client.LangKubeEvent;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.generator.KubeDataGenerator;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.StringUtilsWrapper;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

@ReturnsSelf
public abstract class BuilderBase<T> implements Supplier<T> {
	public final Identifier id;
	public SourceLine sourceLine;
	public ResourceKey<Registry<T>> registryKey;
	protected @Nullable T object;
	public String translationKey;
	public @Nullable Component displayName;
	public boolean formattedDisplayName;
	public transient boolean dummyBuilder;
	public transient Set<Identifier> defaultTags;

	public BuilderBase(Identifier id) {
		this.id = id;
		this.sourceLine = SourceLine.UNKNOWN;
		this.object = null;
		this.translationKey = "";
		this.displayName = null;
		this.formattedDisplayName = false;
		this.dummyBuilder = false;
		this.defaultTags = new HashSet<>();
	}

	@HideFromJS
	public abstract T createObject();

	@HideFromJS
	public T transformObject(T obj) {
		return obj;
	}

	@Override
	@SuppressWarnings("DataFlowIssue")
	public final T get() {
		try {
			return object;
		} catch (Exception ex) {
			if (dummyBuilder) {
				throw new KubeRuntimeException("Object '" + id + "' of registry '" + registryKey.identifier() + "' is from a dummy builder and doesn't have a value!").source(sourceLine);
			} else {
				throw new KubeRuntimeException("Object '" + id + "' of registry '" + registryKey.identifier() + "' hasn't been registered yet!", ex).source(sourceLine);
			}
		}
	}

	@HideFromJS
	public void createAdditionalObjects(AdditionalObjectRegistry registry) {
	}

	public String getTranslationKeyGroup() {
		if (registryKey == null) {
			return "unknown_registry";
		}

		return registryKey.identifier().getPath().replace('/', '.');
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
	public BuilderBase<T> tag(Identifier[] tag) {
		defaultTags.addAll(Arrays.asList(tag));
		return this;
	}

	@HideFromJS
	public Identifier newID(String pre, String post) {
		if (pre.isEmpty() && post.isEmpty()) {
			return id;
		}

		return id.withPath(pre + id.getPath() + post);
	}

	@HideFromJS
	public void generateData(KubeDataGenerator generator) {
	}

	@HideFromJS
	public void generateAssets(KubeAssetGenerator generator) {
	}

	public String getBuilderTranslationKey() {
		if (translationKey.isEmpty()) {
			return Util.makeDescriptionId(getTranslationKeyGroup(), id);
		}

		return translationKey;
	}

	@HideFromJS
	public void generateLang(LangKubeEvent lang) {
		if (displayName != null) {
			lang.add(id.getNamespace(), getBuilderTranslationKey(), displayName.getString());
		} else {
			lang.add(id.getNamespace(), getBuilderTranslationKey(), StringUtilsWrapper.snakeCaseToTitleCase(id.getPath()));
		}
	}

	@HideFromJS
	public T createTransformedObject() {
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
