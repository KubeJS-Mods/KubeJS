package dev.latvian.mods.kubejs.recipe.schema;

import com.mojang.brigadier.StringReader;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RecipeSchemaStorage {
	public final Map<ResourceLocation, KubeRecipeFactory> recipeTypes;
	public final Map<String, RecipeNamespace> namespaces;
	public final Map<String, ResourceLocation> mappings;
	public final Map<String, RecipeComponent<?>> simpleComponents;
	public final Map<String, RecipeComponentFactory> dynamicComponents;
	private final Map<String, RecipeComponent<?>> componentCache;
	public RecipeSchema shapedSchema;
	public RecipeSchema shapelessSchema;
	public RecipeSchema specialSchema;

	public RecipeSchemaStorage() {
		this.recipeTypes = new HashMap<>();
		this.namespaces = new HashMap<>();
		this.mappings = new HashMap<>();
		this.simpleComponents = new HashMap<>();
		this.dynamicComponents = new HashMap<>();
		this.componentCache = new HashMap<>();
	}

	public RecipeNamespace namespace(String namespace) {
		return namespaces.computeIfAbsent(namespace, n -> new RecipeNamespace(this, n));
	}

	public void fireEvents(ResourceManager resourceManager) {
		recipeTypes.clear();
		namespaces.clear();
		mappings.clear();
		simpleComponents.clear();
		dynamicComponents.clear();
		componentCache.clear();
		shapedSchema = null;
		shapelessSchema = null;
		specialSchema = null;

		var typeEvent = new RecipeFactoryRegistryKubeEvent(this);
		KubeJSPlugins.forEachPlugin(typeEvent, KubeJSPlugin::registerRecipeFactories);

		var mappingEvent = new RecipeMappingRegistryKubeEvent(this);
		KubeJSPlugins.forEachPlugin(mappingEvent, KubeJSPlugin::registerRecipeMappings);
		ServerEvents.RECIPE_MAPPING_REGISTRY.post(ScriptType.SERVER, mappingEvent);

		KubeJSPlugins.forEachPlugin(new RecipeComponentFactoryRegistryEvent(this), KubeJSPlugin::registerRecipeComponents);

		for (var entry : RegistryInfo.RECIPE_SERIALIZER.entrySet()) {
			var ns = namespace(entry.getKey().location().getNamespace());
			ns.put(entry.getKey().location().getPath(), new UnknownRecipeSchemaType(ns, entry.getKey().location(), entry.getValue()));
		}

		var schemaRegistryEvent = new RecipeSchemaRegistryKubeEvent(this);
		JsonRecipeSchemaLoader.load(this, schemaRegistryEvent, resourceManager);

		shapedSchema = Objects.requireNonNull(namespace("minecraft").get("shaped").schema);
		shapelessSchema = Objects.requireNonNull(namespace("minecraft").get("shapeless").schema);
		specialSchema = Objects.requireNonNull(namespace("minecraft").get("special").schema);

		KubeJSPlugins.forEachPlugin(schemaRegistryEvent, KubeJSPlugin::registerRecipeSchemas);
		ServerEvents.RECIPE_SCHEMA_REGISTRY.post(ScriptType.SERVER, schemaRegistryEvent);
	}

	public RecipeComponent<?> getComponent(String string) {
		var c = componentCache.get(string);

		if (c == null) {
			try {
				c = readComponent(new StringReader(string));
				componentCache.put(string, c);
			} catch (Exception ex) {
				throw new IllegalArgumentException(ex);
			}
		}

		return c;
	}

	public RecipeComponent<?> readComponent(StringReader reader) throws Exception {
		reader.skipWhitespace();
		var key = reader.readUnquotedString();
		RecipeComponent<?> component = simpleComponents.get(key);

		if (component == null) {
			var d = dynamicComponents.get(key);

			if (d != null) {
				component = d.readComponent(this, reader);
			}
		}

		if (component == null) {
			throw new UnsupportedOperationException("Recipe Component '" + key + "' not found");
		}

		reader.skipWhitespace();

		while (reader.canRead() && reader.peek() == '[') {
			reader.skip();
			reader.skipWhitespace();
			boolean self = reader.canRead() && reader.peek() == '?';

			if (self) {
				reader.skip();
				reader.skipWhitespace();
			}

			reader.expect(']');
			component = self ? component.asListOrSelf() : component.asList();
		}

		return component;
	}
}
