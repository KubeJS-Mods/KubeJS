package dev.latvian.mods.kubejs.recipe.schema;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.kubejs.codec.KubeJSCodecs;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.plugin.builtin.event.ServerEvents;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentCodecFactory;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentTypeRegistry;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.JsonUtils;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.DelegatingOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class RecipeSchemaStorage {
	public static class Ops<T> extends DelegatingOps<T> {
		public final RecipeSchemaStorage storage;
		public final RegistryAccessContainer registries;
		public final RecipeComponentCodecFactory.Context recipeComponentCodecFactoryContext;

		private Ops(DynamicOps<T> delegate, RecipeSchemaStorage storage, RegistryAccessContainer registries, RecipeComponentCodecFactory.Context recipeComponentCodecFactoryContext) {
			super(delegate);
			this.storage = storage;
			this.registries = registries;
			this.recipeComponentCodecFactoryContext = recipeComponentCodecFactoryContext;
		}
	}

	public final Map<ResourceLocation, KubeRecipeFactory> recipeTypes;
	public final Map<String, RecipeNamespace> namespaces;
	public final Map<String, ResourceLocation> mappings;
	public final Map<String, RecipeSchemaType> schemaTypes;
	public RecipeSchema shapedSchema;
	public RecipeSchema shapelessSchema;
	public RecipeSchema specialSchema;

	public RecipeSchemaStorage() {
		this.recipeTypes = new HashMap<>();
		this.namespaces = new HashMap<>();
		this.mappings = new HashMap<>();
		this.schemaTypes = new HashMap<>();
	}

	public RecipeNamespace namespace(String namespace) {
		return namespaces.computeIfAbsent(namespace, n -> new RecipeNamespace(this, n));
	}

	public void fireEvents(RegistryAccessContainer registries, ResourceManager resourceManager) {
		recipeTypes.clear();
		namespaces.clear();
		mappings.clear();
		schemaTypes.clear();
		shapedSchema = null;
		shapelessSchema = null;
		specialSchema = null;

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

		var componentTypes = new HashMap<ResourceLocation, RecipeComponentType<?>>();
		var rcCtx = new RecipeComponentCodecFactory.Context(registries, this, KubeJSCodecs.KUBEJS_ID.xmap(componentTypes::get, RecipeComponentType::id), new MutableObject<>());

		rcCtx.unsetCodec().setValue(
			Codec.either(
				rcCtx.typeCodec(), // .validate that it's a unit type
				rcCtx.typeCodec().dispatch("type", RecipeComponent::type, type -> type.mapCodec(rcCtx))
			).xmap(either -> either.map(RecipeComponentType::instance, Function.identity()), component -> {
				if (component.type().isUnit()) {
					return Either.left(component.type());
				} else {
					return Either.right(component);
				}
			})
		);

		KubeJSPlugins.forEachPlugin(new RecipeComponentTypeRegistry(componentTypes), KubeJSPlugin::registerRecipeComponents);

		var jsonOps = new Ops<>(registries.json(), this, registries, rcCtx);

		for (var entry : resourceManager.listResources("kubejs", path -> path.getPath().endsWith("/recipe_components.json")).entrySet()) {
			try (var reader = entry.getValue().openAsReader()) {
				var json = JsonUtils.GSON.fromJson(reader, JsonObject.class);

				for (var entry1 : json.entrySet()) {
					var id = ID.kjs(entry1.getKey());
					var component = rcCtx.codec().decode(jsonOps, entry1.getValue()).getOrThrow().getFirst();
					componentTypes.put(id, RecipeComponentType.unit(id, component));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		for (var entry : BuiltInRegistries.RECIPE_SERIALIZER.entrySet()) {
			var ns = namespace(entry.getKey().location().getNamespace());
			ns.put(entry.getKey().location().getPath(), new UnknownRecipeSchemaType(ns, entry.getKey().location(), entry.getValue()));
		}

		var schemaRegistry = new RecipeSchemaRegistry(this);
		JsonRecipeSchemaLoader.load(jsonOps, schemaRegistry, resourceManager);

		shapedSchema = Objects.requireNonNull(namespace("minecraft").get("shaped").schema);
		shapelessSchema = Objects.requireNonNull(namespace("minecraft").get("shapeless").schema);
		specialSchema = Objects.requireNonNull(namespace("minecraft").get("special").schema);

		KubeJSPlugins.forEachPlugin(schemaRegistry, KubeJSPlugin::registerRecipeSchemas);
		ServerEvents.RECIPE_SCHEMA_REGISTRY.post(ScriptType.SERVER, schemaRegistry);
	}
}
