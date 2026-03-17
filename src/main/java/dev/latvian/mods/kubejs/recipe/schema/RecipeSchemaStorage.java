package dev.latvian.mods.kubejs.recipe.schema;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DataResult.Error;
import com.mojang.serialization.DataResult.Success;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
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
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.HashMap;
import java.util.Map;

public class RecipeSchemaStorage {
	private static final BiMap<ResourceKey<RecipeComponentType<?>>, RecipeComponentType<?>> COMPONENT_TYPES = HashBiMap.create();

	private final ServerScriptManager manager;

	public final Map<Identifier, KubeRecipeFactory> recipeTypes;
	public final Map<String, RecipeNamespace> namespaces;
	public final Map<String, Identifier> mappings;
	public final Map<String, RecipeSchemaType> schemaTypes;

	public static DataResult<RecipeComponentType<?>> getType(ResourceKey<RecipeComponentType<?>> key) {
		var stored = COMPONENT_TYPES.get(key);
		return stored != null
			? DataResult.success(stored)
			: DataResult.error(() -> "Unknown recipe component type '%s'".formatted(ID.reduceKjs(key.identifier())));
	}

	public static DataResult<RecipeComponentType<?>> getType(Identifier id) {
		return getType(RecipeComponentType.key(id));
	}

	public static DataResult<RecipeComponentType<?>> getType(RecipeComponent<?> component) {
		return getType(component.type());
	}

	private static DataResult<ResourceKey<RecipeComponentType<?>>> getTypeId(RecipeComponentType<?> type) {
		var id = COMPONENT_TYPES.inverse().get(type);
		return id != null
			? DataResult.success(id)
			: DataResult.error(() -> "Unregistered recipe component type %s???".formatted(type));
	}

	private static final Codec<RecipeComponentType<?>> TYPE_CODEC = KubeJSCodecs.KUBEJS_ID
		.xmap(RecipeComponentType::key, ResourceKey::identifier)
		.flatXmap(RecipeSchemaStorage::getType, RecipeSchemaStorage::getTypeId);

	private static final Codec<RecipeComponent<?>> MAP_CODEC = TYPE_CODEC.partialDispatch("type",
		RecipeSchemaStorage::getType,
		type -> DataResult.success(type.mapCodec())
	);

	public static final Codec<RecipeComponent<?>> COMPONENT_CODEC = new Codec<>() {
		@Override
		public <T> DataResult<Pair<RecipeComponent<?>, T>> decode(final DynamicOps<T> ops, final T input) {
			DataResult<Pair<RecipeComponent<?>, T>> fromType = TYPE_CODEC.decode(ops, input)
				.flatMap(pair -> pair.getFirst().mapCodec().decode(ops, MapLike.empty())
					.map(result -> Pair.of(result, pair.getSecond())));

			if (fromType.isSuccess()) {
				return fromType;
			}

			return MAP_CODEC.decode(ops, input)
				.mapError(err -> "Failed to parse component. Input is not a unit type; map codec error: " + err);
		}

		@Override
		public <T> DataResult<T> encode(final RecipeComponent<?> input, final DynamicOps<T> ops, final T prefix) {
			return MAP_CODEC.encode(input, ops, prefix);
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

		COMPONENT_TYPES.clear();

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

		KubeJSPlugins.forEachPlugin(COMPONENT_TYPES::put, KubeJSPlugin::registerRecipeComponents);

		var ops = registries.json();
		for (var entry : resourceManager.listResources("kubejs", path -> path.getPath().endsWith("/recipe_components.json")).entrySet()) {
			try (var reader = entry.getValue().openAsReader()) {
				var json = JsonUtils.GSON.fromJson(reader, JsonObject.class);

				for (var componentDef : json.entrySet()) {
					var id = ID.kjs(componentDef.getKey());

					// TODO: i'm not sure but i think json recipe components can't use each other now??
					switch (COMPONENT_CODEC.parse(ops, componentDef.getValue())) {
						case Success(var value, _) -> COMPONENT_TYPES.put(RecipeComponentType.key(id), () -> MapCodec.unit(value));
						case Error(var msg, _, _) -> KubeJS.LOGGER.error("Failed to load recipe component {} from {}: {}", id, entry.getKey(), msg.get());
					}
				}
			} catch (Exception ex) {
				KubeJS.LOGGER.error("Failed to load recipe component file {}: {}", entry.getKey(), ex);
			}
		}

		for (var entry : BuiltInRegistries.RECIPE_SERIALIZER.entrySet()) {
			var id = entry.getKey().identifier();

			var ns = namespace(id.getNamespace());
			ns.put(id.getPath(), new UnknownRecipeSchemaType(ns, id, entry.getValue()));
		}

		var schemaRegistry = new RecipeSchemaRegistry(this);
		JsonRecipeSchemaLoader.load(ops, this, schemaRegistry, resourceManager);

		KubeJSPlugins.forEachPlugin(schemaRegistry, KubeJSPlugin::registerRecipeSchemas);
		ServerEvents.RECIPE_SCHEMA_REGISTRY.post(ScriptType.SERVER, schemaRegistry);
	}
}
