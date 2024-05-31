package dev.latvian.mods.kubejs.registry;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.event.KubeStartupEvent;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.ID;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class RegistryKubeEvent<T> implements KubeStartupEvent, AdditionalObjectRegistry {
	private final RegistryInfo<T> registryInfo;
	public final List<BuilderBase<? extends T>> created;

	public RegistryKubeEvent(ResourceKey<Registry<T>> registryKey) {
		this.registryInfo = RegistryInfo.of(registryKey);
		this.created = new LinkedList<>();
	}

	public BuilderBase<? extends T> create(String id, String type) {
		var t = registryInfo.types == null ? null : registryInfo.types.get(type);

		if (t == null) {
			throw new IllegalArgumentException("Unknown type '" + type + "' for object '" + id + "'!");
		}

		var b = t.factory().createBuilder(ID.kjs(id));

		if (b == null) {
			throw new IllegalArgumentException("Unknown type '" + type + "' for object '" + id + "'!");
		} else {
			addBuilder(registryInfo, b);
			created.add(b);
		}

		return b;
	}

	public BuilderBase<? extends T> create(String id) {
		var t = registryInfo.defaultType;

		if (t == null) {
			throw new IllegalArgumentException("Registry '" + registryInfo.key.location() + "' doesn't have a default type registered!");
		}

		var b = t.factory().createBuilder(ID.kjs(id));

		if (b == null) {
			throw new IllegalArgumentException("Unknown type '" + t.type() + "' for object '" + id + "'!");
		} else {
			addBuilder(registryInfo, b);
			created.add(b);
		}

		return b;
	}

	public CustomBuilderObject createCustom(String id, Supplier<Object> object) {
		if (object == null) {
			throw new IllegalArgumentException("Tried to register a null object with id: " + id);
		}

		var rl = ID.kjs(id);
		var b = new CustomBuilderObject(rl, object, registryInfo);
		addBuilder(registryInfo, b);
		created.add(b);
		return b;
	}

	@Override
	public void afterPosted(EventResult result) {
		for (var c : created) {
			c.createAdditionalObjects(this);
		}
	}

	@Override
	public <R> void add(ResourceKey<Registry<R>> registry, BuilderBase<? extends R> builder) {
		addBuilder(RegistryInfo.of(registry), builder);
	}

	@Override
	public <R> void add(RegistryInfo<R> registry, BuilderBase<? extends R> builder) {
		addBuilder(registry, builder);
	}

	private <R> void addBuilder(RegistryInfo<R> registry, BuilderBase<? extends R> builder) {
		if (builder == null) {
			throw new IllegalArgumentException("Can't add null builder in registry '" + registry + "'!");
		}

		if (DevProperties.get().debugInfo) {
			ConsoleJS.STARTUP.info("~ " + registry + " | " + builder.id);
		}

		if (registry.objects.containsKey(builder.id)) {
			throw new IllegalArgumentException("Duplicate key '" + builder.id + "' in registry '" + registry + "'!");
		}

		registry.objects.put(builder.id, builder);
		RegistryInfo.ALL_BUILDERS.add(builder);

		// registry.deferredRegister.register()
	}
}