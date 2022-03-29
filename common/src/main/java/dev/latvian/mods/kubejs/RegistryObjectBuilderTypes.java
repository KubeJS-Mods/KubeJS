package dev.latvian.mods.kubejs;

import dev.architectury.registry.registries.DeferredRegister;
import dev.latvian.mods.kubejs.event.StartupEventJS;
import dev.latvian.mods.kubejs.util.BuilderBase;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RegistryObjectBuilderTypes<T> {
	public interface BuilderFactory<T> {
		BuilderBase<T> createBuilder(ResourceLocation id);
	}

	public record BuilderType<T>(String type, Class<? extends BuilderBase<T>> builderClass, BuilderFactory<T> factory) {
	}

	public static class RegistryEventJS<T> extends StartupEventJS {
		private final RegistryObjectBuilderTypes<T> registry;
		private final List<BuilderBase<T>> builders;

		private RegistryEventJS(RegistryObjectBuilderTypes<T> r) {
			registry = r;
			builders = new ArrayList<>();
		}

		public BuilderBase<T> create(String id, String type) {
			var b = registry.createBuilder(UtilsJS.getMCID(KubeJS.appendModId(id)), type);

			if (b == null) {
				throw new IllegalArgumentException("Unknown type '" + type + "' for item '" + id + "'!");
			} else {
				builders.add(b);
			}

			return b;
		}

		public BuilderBase<T> create(String id) {
			return create(id, registry.defaultBuilder);
		}
	}

	public static final Map<ResourceKey<?>, RegistryObjectBuilderTypes<?>> MAP = new HashMap<>();
	public static final List<BuilderBase<?>> ALL_BUILDERS = new ArrayList<>();

	public static <T> RegistryObjectBuilderTypes<T> add(ResourceKey<Registry<T>> key, Class<T> baseClass, String def) {
		RegistryObjectBuilderTypes<T> types = new RegistryObjectBuilderTypes<>(key, baseClass, def);
		MAP.put(key, types);
		return types;
	}

	public static final RegistryObjectBuilderTypes<Block> BLOCK = add(Registry.BLOCK_REGISTRY, Block.class, "basic");
	public static final RegistryObjectBuilderTypes<Item> ITEM = add(Registry.ITEM_REGISTRY, Item.class, "basic");
	public static final RegistryObjectBuilderTypes<Fluid> FLUID = add(Registry.FLUID_REGISTRY, Fluid.class, "basic");

	public final ResourceKey<Registry<T>> registryKey;
	public final Class<T> objectBaseClass;
	public final String defaultBuilder;
	public final DeferredRegister<T> deferredRegister;
	public final Map<String, BuilderType<T>> types;
	public final Map<ResourceLocation, BuilderBase<T>> objects;
	private BuilderBase<T> current;
	public boolean bypassServerOnly;

	private RegistryObjectBuilderTypes(ResourceKey<Registry<T>> key, Class<T> baseClass, String def) {
		registryKey = key;
		objectBaseClass = baseClass;
		defaultBuilder = def;
		deferredRegister = DeferredRegister.create(KubeJS.MOD_ID, registryKey);
		types = new HashMap<>();
		objects = new HashMap<>();
		current = null;
		bypassServerOnly = false;
	}

	public RegistryObjectBuilderTypes<T> bypassServerOnly() {
		bypassServerOnly = true;
		return this;
	}

	public void addType(String type, Class<? extends BuilderBase<T>> builderType, BuilderFactory<T> factory) {
		types.put(type, new BuilderType<>(type, builderType, factory));
	}

	public <B extends BuilderBase<T>> B addBuilder(B builder) {
		if (builder != null) {
			if (objects.containsKey(builder.id)) {
				throw new IllegalArgumentException("Duplicate key '" + builder.id + "' in registry '" + registryKey.location() + "'!");
			}

			objects.put(builder.id, builder);
			ALL_BUILDERS.add(builder);
			return builder;
		}

		return null;
	}

	public BuilderBase<T> createBuilder(ResourceLocation id, String type) {
		var t = types.get(type);
		return t == null ? null : addBuilder(t.factory.createBuilder(id));
	}

	public void postEvent(String id) {
		var event = new RegistryEventJS<>(this);
		event.post(id);

		for (var builder : event.builders) {
			builder.createAdditionalObjects();
		}
	}

	private void register() {
		for (var builder : objects.values()) {
			if (!builder.dummyBuilder) {
				current = builder;
				builder.object = deferredRegister.register(builder.id, builder::createTransformedObject);
			}
		}

		current = null;
	}

	public static void registerAll(boolean all) {
		for (var types : MAP.values()) {
			if (all || types.bypassServerOnly) {
				types.register();
			}
		}
	}

	@Nullable
	public BuilderBase<T> getCurrent() {
		return current;
	}
}
