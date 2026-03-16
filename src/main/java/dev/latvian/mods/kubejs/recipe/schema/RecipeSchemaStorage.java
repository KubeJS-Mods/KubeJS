package dev.latvian.mods.kubejs.recipe.schema;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.codec.KubeJSCodecs;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.plugin.builtin.event.ServerEvents;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.JsonUtils;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class RecipeSchemaStorage {
	public static final class StoredRecipeComponentType {
		private final RecipeComponentType<?> type;
		private MapCodec<RecipeComponent<?>> mapCodec;
		private RecipeComponent<?> unit;

		public StoredRecipeComponentType(RecipeComponentType<?> type) {
			this.type = type;
		}

		@Override
		public @NotNull String toString() {
			return type.toString();
		}

		public Identifier id() {
			return type.id();
		}

		void init(Ops ops) {
			mapCodec = type.mapCodec();
			unit = type.isUnit() ? type.instance() : mapCodec.decode(ops, JsonUtils.MAP_LIKE).result().orElse(null);
		}
	}

	private final ServerScriptManager manager;

	public final Map<Identifier, KubeRecipeFactory> recipeTypes;
	public final Map<String, RecipeNamespace> namespaces;
	public final Map<String, Identifier> mappings;
	public final Map<String, RecipeSchemaType> schemaTypes;

	private static <T> DataResult<StoredRecipeComponentType> retrieveStored(DynamicOps<T> ops, Identifier id) {
		if (ops instanceof Ops ctx) {
			var stored = ctx.componentTypes.get(id);
			return stored != null
				? DataResult.success(stored)
				: DataResult.error(() -> "Unknown recipe component type '%s'".formatted(ID.reduceKjs(id)));
		}
		return DataResult.error(() -> "Not in a recipe schema registry context!");
	}

	private static final Codec<RecipeComponentType<?>> TYPE_CODEC = new Codec<>() {
		@Override
		public <T> DataResult<Pair<RecipeComponentType<?>, T>> decode(DynamicOps<T> ops, T input) {
			return KubeJSCodecs.KUBEJS_ID.decode(ops, input)
				.flatMap(pair -> retrieveStored(ops, pair.getFirst())
					.map(stored -> Pair.of(stored.type, pair.getSecond())));
		}

		@Override
		public <T> DataResult<T> encode(RecipeComponentType<?> input, DynamicOps<T> ops, T prefix) {
			return KubeJSCodecs.KUBEJS_ID.encode(input.id(), ops, prefix);
		}
	};

	private static final Codec<RecipeComponent<?>> COMPONENT_BY_TYPE = TYPE_CODEC.dispatch("type", RecipeComponent::type, RecipeComponentType::mapCodec);

	public static final Codec<RecipeComponent<?>> COMPONENT_CODEC = new Codec<>() {
		@Override
		public <T> DataResult<Pair<RecipeComponent<?>, T>> decode(DynamicOps<T> ops, T input) {
			DataResult<Pair<RecipeComponent<?>, T>> unitResult = TYPE_CODEC.decode(ops, input)
				.flatMap(pair -> retrieveStored(ops, pair.getFirst().id())
					.flatMap(stored -> {
						if (stored.unit == null) {
							return DataResult.error(() -> "Dynamic recipe component type '%s' doesn't have a unit value".formatted(ID.reduceKjs(stored.id())));
						}

						return DataResult.success(Pair.of(stored.unit, pair.getSecond()));
					}));

			return unitResult.result().isPresent() ? unitResult : COMPONENT_BY_TYPE.decode(ops, input);
		}

		@Override
		public <T> DataResult<T> encode(RecipeComponent<?> input, DynamicOps<T> ops, T prefix) {
			return input.type().isUnit()
				? TYPE_CODEC.encode(input.type(), ops, prefix)
				: COMPONENT_BY_TYPE.encode(input, ops, prefix);
		}
	};

	public RecipeSchemaStorage(ServerScriptManager manager) {
		this.manager = manager;
		this.recipeTypes = new HashMap<>();
		this.namespaces = new HashMap<>();
		this.mappings = new HashMap<>();
		this.schemaTypes = new HashMap<>();
	}

	public RecipeNamespace namespace(String namespace) {
		return namespaces.computeIfAbsent(namespace, n -> new RecipeNamespace(this, n));
	}

	RegistryAccessContainer registries() {
		return manager.getRegistries();
	}

	public void fireEvents(RegistryAccessContainer registries, ResourceManager resourceManager) {
		recipeTypes.clear();
		namespaces.clear();
		mappings.clear();
		schemaTypes.clear();

		var componentTypes = new HashMap<Identifier, StoredRecipeComponentType>();
		var ops = new Ops(registries.json(), this, componentTypes);

		var typeEvent = new RecipeFactoryRegistry(this);
		KubeJSPlugins.forEachPlugin(typeEvent, KubeJSPlugin::registerRecipeFactories);

		for (var entry : resourceManager.listResources("kubejs", path -> path.getPath().endsWith("/recipe_mappings.json")).entrySet()) {
			try (var reader = entry.getValue().openAsReader()) {
				var json = JsonUtils.GSON.fromJson(reader, JsonObject.class);

				for (var entry1 : json.entrySet()) {
					var id = Identifier.fromNamespaceAndPath(entry.getKey().getNamespace(), entry1.getKey());

					if (entry1.getValue() instanceof JsonArray arr) {
						for (var n : arr) {
							mappings.put(n.getAsString(), id);
						}
					} else {
						mappings.put(entry1.getValue().getAsString(), id);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		var mappingRegistry = new RecipeMappingRegistry(this);
		KubeJSPlugins.forEachPlugin(mappingRegistry, KubeJSPlugin::registerRecipeMappings);
		ServerEvents.RECIPE_MAPPING_REGISTRY.post(ScriptType.SERVER, mappingRegistry);

		KubeJSPlugins.forEachPlugin(type -> componentTypes.put(type.id(), new StoredRecipeComponentType(type)), KubeJSPlugin::registerRecipeComponents);

		for (var stored : componentTypes.values()) {
			stored.init(ops);
		}

		for (var entry : resourceManager.listResources("kubejs", path -> path.getPath().endsWith("/recipe_components.json")).entrySet()) {
			try (var reader = entry.getValue().openAsReader()) {
				var json = JsonUtils.GSON.fromJson(reader, JsonObject.class);

				for (var entry1 : json.entrySet()) {
					var id = ID.kjs(entry1.getKey());
					var componentResult = COMPONENT_CODEC.parse(ops, entry1.getValue());

					if (componentResult.isSuccess()) {
						var stored = new StoredRecipeComponentType(RecipeComponentType.unit(id, componentResult.getOrThrow()));
						componentTypes.put(id, stored);
						stored.init(ops);
					} else {
						KubeJS.LOGGER.error("Failed to load recipe component {} from {}: {}", id, entry.getKey(), componentResult.error().map(DataResult.Error::message).orElse("Unknown Error"));
					}
				}
			} catch (Exception ex) {
				KubeJS.LOGGER.error("Failed to load recipe component file {}: {}", entry.getKey(), ex);
			}
		}

		for (var entry : BuiltInRegistries.RECIPE_SERIALIZER.entrySet()) {
			var ns = namespace(entry.getKey().identifier().getNamespace());
			ns.put(entry.getKey().identifier().getPath(), new UnknownRecipeSchemaType(ns, entry.getKey().identifier(), entry.getValue()));
		}

		var schemaRegistry = new RecipeSchemaRegistry(this);
		JsonRecipeSchemaLoader.load(ops, schemaRegistry, resourceManager);

		KubeJSPlugins.forEachPlugin(schemaRegistry, KubeJSPlugin::registerRecipeSchemas);
		ServerEvents.RECIPE_SCHEMA_REGISTRY.post(ScriptType.SERVER, schemaRegistry);
	}

	static class Ops extends RegistryOps<JsonElement> {
		final RecipeSchemaStorage storage;

		private final Map<Identifier, StoredRecipeComponentType> componentTypes;

		Ops(
			RegistryOps<JsonElement> other,
			RecipeSchemaStorage storage,
			Map<Identifier, StoredRecipeComponentType> componentTypes
		) {
			super(other);
			this.storage = storage;
			this.componentTypes = componentTypes;
		}
	}
}
