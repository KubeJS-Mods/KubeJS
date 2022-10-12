package dev.latvian.mods.kubejs;

import dev.architectury.registry.registries.RegistrySupplier;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.generator.DataJsonGenerator;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author LatvianModder
 */
public abstract class BuilderBase<T> implements Supplier<T> {
	public final ResourceLocation id;
	private RegistrySupplier<T> object;
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

	public abstract RegistryObjectBuilderTypes<? super T> getRegistryType();

	public abstract T createObject();

	public T transformObject(T obj) {
		return obj;
	}

	@Override
	public final T get() {
		try {
			return object.get();
		} catch (Exception ex) {
			if (dummyBuilder) {
				throw new RuntimeException("Object '" + id + "' of registry '" + getRegistryType().registryKey.location() + "' is from a dummy builder and doesn't have a value!");
			} else {
				throw new RuntimeException("Object '" + id + "' of registry '" + getRegistryType().registryKey.location() + "' hasn't been registered yet!");
			}
		}
	}

	public void createAdditionalObjects() {
	}

	public String getTranslationKeyGroup() {
		return getRegistryType().registryKey.location().getPath();
	}

	public BuilderBase<T> translationKey(String key) {
		translationKey = key;
		return this;
	}

	public BuilderBase<T> displayName(String name) {
		displayName = name;
		return this;
	}

	public BuilderBase<T> tag(ResourceLocation tag) {
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

	@Environment(EnvType.CLIENT)
	public void clientRegistry(Supplier<Minecraft> minecraft) {
	}

	public void addResourcePackLocations(String path, List<ResourceLocation> list, PackType packType) {
	}

	private T createTransformedObject() {
		getRegistryType().current = this;
		T o = transformObject(createObject());
		getRegistryType().current = null;
		return o;
	}

	final boolean registerObject(boolean all) {
		if (!dummyBuilder && (all || getRegistryType().bypassServerOnly)) {
			if (CommonProperties.get().debugInfo) {
				ConsoleJS.STARTUP.info("+ " + getRegistryType().registryKey.location() + " | " + id);
			}

			object = getRegistryType().deferredRegister.register(id, this::createTransformedObject);
			return true;
		}

		return false;
	}

	protected final RegistrySupplier<T> asRegistrySupplier() {
		return Objects.requireNonNull(object, () -> "Object '%s' of registry '%s' hasn't been registered yet!".formatted(id, getRegistryType().registryKey.location()));
	}

	// Class that acts as a holder for a custom registry object, usually based off a class from another mod.
	static class CustomBuilderObject<T> extends BuilderBase<T> {

		private final T object;
		private final RegistryObjectBuilderTypes<T> registry;

		public CustomBuilderObject(ResourceLocation i, T object, RegistryObjectBuilderTypes<T> registry) {
			super(i);
			this.object = object;
			this.registry = registry;
			// This, along with overriding getTranslationKeyGroup, is to avoid a crash occurring from getRegistryType returning null
			// when called from the super constructor, because the value it returns is not set until after the super constructor is run
			this.translationKey = getTranslationKeyGroup();
		}

		@Override
		public String getTranslationKeyGroup() {
			if (getRegistryType() == null) {
				return "If you see this something broke. Please file a bug report.";
			} else {
				return super.getTranslationKeyGroup();
			}
		}

		@Override
		public RegistryObjectBuilderTypes<? super T> getRegistryType() {
			return registry;
		}

		@Override
		public T createObject() {
			return object;
		}
	}
}
