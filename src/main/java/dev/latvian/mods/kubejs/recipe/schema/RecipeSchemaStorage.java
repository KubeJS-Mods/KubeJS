package dev.latvian.mods.kubejs.recipe.schema;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.codec.KubeJSCodecs;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.plugin.builtin.event.ServerEvents;
import dev.latvian.mods.kubejs.recipe.RecipeTypeRegistryContext;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.schema.postprocessing.RecipePostProcessor;
import dev.latvian.mods.kubejs.recipe.schema.postprocessing.RecipePostProcessorType;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.JsonUtils;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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

		public void init(RecipeTypeRegistryContext ctx) {
			mapCodec = type.mapCodec(ctx);
			unit = type.isUnit() ? type.instance() : mapCodec.decode(JsonOps.INSTANCE, JsonUtils.MAP_LIKE).result().orElse(null);
		}
	}

	private final ServerScriptManager manager;

	public final Map<ResourceLocation, KubeRecipeFactory> recipeTypes;
	public final Map<String, RecipeNamespace> namespaces;
	public final Map<String, ResourceLocation> mappings;
	public final Map<String, RecipeSchemaType> schemaTypes;

	public Codec<RecipeComponent<?>> recipeComponentCodec;
	public Codec<RecipePostProcessor> recipePostProcessorCodec;

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

	RegistryAccessContainer getRegistries() {
		return manager.getRegistries();
	}

	public void fireEvents(RegistryAccessContainer registries, ResourceManager resourceManager) {
		recipeTypes.clear();
		namespaces.clear();
		mappings.clear();
		schemaTypes.clear();

		var jsonOps = registries.json();

		var typeEvent = new RecipeFactoryRegistry(this);
		KubeJSPlugins.forEachPlugin(typeEvent, KubeJSPlugin::registerRecipeFactories);

		for (var entry : resourceManager.listResources("kubejs", path -> path.getPath().endsWith("/recipe_mappings.json")).entrySet()) {
			try (var reader = entry.getValue().openAsReader()) {
				var json = JsonUtils.GSON.fromJson(reader, JsonObject.class);

				for (var entry1 : json.entrySet()) {
					var id = ResourceLocation.fromNamespaceAndPath(entry.getKey().getNamespace(), entry1.getKey());

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

		var componentTypes = new HashMap<ResourceLocation, StoredRecipeComponentType>();
		Codec<StoredRecipeComponentType> typeCodec = KubeJSCodecs.KUBEJS_ID.comapFlatMap(id -> {
			var stored = componentTypes.get(id);

			if (stored != null) {
				return DataResult.success(stored);
			} else {
				return DataResult.error(() -> "Unknown recipe component type '" + ID.reduceKjs(id) + "'");
			}
		}, stored -> stored.type.id());

		Codec<RecipeComponent<?>> directComponentCodec = typeCodec.partialDispatch("type", c -> {
			var stored = componentTypes.get(c.type().id());

			if (stored != null) {
				return DataResult.success(stored);
			} else {
				return DataResult.error(() -> "Missing stored recipe component type for '" + ID.reduceKjs(c.type().id()) + "'");
			}
		}, type -> DataResult.success(type.mapCodec));

		recipeComponentCodec = Codec.either(
			typeCodec,
			directComponentCodec
		).comapFlatMap(either -> either.map(stored -> {
			if (stored.unit != null) {
				return DataResult.success(stored.unit);
			} else {
				// return DataResult.error(() -> "Dynamic recipe component type '" + ID.reduceKjs(stored.type.id()) + "' doesn't have a unit value");
				return stored.mapCodec.decode(jsonOps, JsonUtils.MAP_LIKE);
			}
		}, DataResult::success), component -> {
			if (component.type().isUnit()) {
				return Either.left(componentTypes.get(component.type().id()));
			} else {
				return Either.right(component);
			}
		});

		KubeJSPlugins.forEachPlugin(type -> componentTypes.put(type.id(), new StoredRecipeComponentType(type)), KubeJSPlugin::registerRecipeComponents);

		var rcCtx = new RecipeTypeRegistryContext(registries, this);

		for (var stored : componentTypes.values()) {
			stored.init(rcCtx);
		}

		for (var entry : resourceManager.listResources("kubejs", path -> path.getPath().endsWith("/recipe_components.json")).entrySet()) {
			try (var reader = entry.getValue().openAsReader()) {
				var json = JsonUtils.GSON.fromJson(reader, JsonObject.class);

				for (var entry1 : json.entrySet()) {
					var id = ID.kjs(entry1.getKey());
					var componentResult = recipeComponentCodec.parse(jsonOps, entry1.getValue());

					if (componentResult.isSuccess()) {
						var stored = new StoredRecipeComponentType(RecipeComponentType.unit(id, componentResult.getOrThrow()));
						componentTypes.put(id, stored);
						stored.init(rcCtx);
					} else {
						KubeJS.LOGGER.error("Failed to load recipe component {} from {}: {}", id, entry.getKey(), componentResult.error().map(DataResult.Error::message).orElse("Unknown Error"));
					}
				}
			} catch (Exception ex) {
				KubeJS.LOGGER.error("Failed to load recipe component file {}: {}", entry.getKey(), ex);
			}
		}

		recipePostProcessorCodec = RecipePostProcessorType.CODEC.dispatch("type", RecipePostProcessor::type, type -> type.mapCodec().apply(rcCtx));

		for (var entry : BuiltInRegistries.RECIPE_SERIALIZER.entrySet()) {
			var ns = namespace(entry.getKey().location().getNamespace());
			ns.put(entry.getKey().location().getPath(), new UnknownRecipeSchemaType(ns, entry.getKey().location(), entry.getValue()));
		}

		var schemaRegistry = new RecipeSchemaRegistry(this);
		JsonRecipeSchemaLoader.load(rcCtx, jsonOps, schemaRegistry, resourceManager);

		KubeJSPlugins.forEachPlugin(schemaRegistry, KubeJSPlugin::registerRecipeSchemas);
		ServerEvents.RECIPE_SCHEMA_REGISTRY.post(ScriptType.SERVER, schemaRegistry);
	}
}
