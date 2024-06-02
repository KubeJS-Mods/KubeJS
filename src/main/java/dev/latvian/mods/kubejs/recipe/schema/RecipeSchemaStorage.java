package dev.latvian.mods.kubejs.recipe.schema;

import com.mojang.brigadier.StringReader;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.bindings.event.StartupEvents;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class RecipeSchemaStorage {
	public final Map<String, RecipeNamespace> namespaces;
	public final Map<String, ResourceLocation> mappings;
	public final Map<String, RecipeComponent<?>> simpleComponents;
	public final Map<String, RecipeComponentFactory> dynamicComponents;

	public RecipeSchemaStorage() {
		this.namespaces = new HashMap<>();
		this.mappings = new HashMap<>();
		this.simpleComponents = new HashMap<>();
		this.dynamicComponents = new HashMap<>();
	}

	public void fireEvents() {
		namespaces.clear();
		mappings.clear();
		simpleComponents.clear();
		dynamicComponents.clear();

		KubeJSPlugins.forEachPlugin(new RecipeComponentFactoryRegistryEvent(this), KubeJSPlugin::registerRecipeComponents);

		for (var entry : RegistryInfo.RECIPE_SERIALIZER.entrySet()) {
			var ns = namespaces.computeIfAbsent(entry.getKey().location().getNamespace(), RecipeNamespace::new);
			ns.put(entry.getKey().location().getPath(), new JsonRecipeSchemaType(ns, entry.getKey().location(), entry.getValue()));
		}

		var recipeSchemaRegistryKubeEvent = new RecipeSchemaRegistryKubeEvent(this);
		KubeJSPlugins.forEachPlugin(recipeSchemaRegistryKubeEvent, KubeJSPlugin::registerRecipeSchemas);
		StartupEvents.RECIPE_SCHEMA_REGISTRY.post(ScriptType.STARTUP, recipeSchemaRegistryKubeEvent);
	}

	public RecipeComponent<?> getComponent(String string) {
		try {
			return readComponent(new StringReader(string));
		} catch (Exception ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	public RecipeComponent<?> readComponent(StringReader reader) throws Exception {
		reader.skipWhitespace();
		var key = reader.readUnquotedString();
		var s = simpleComponents.get(key);

		if (s != null) {
			return s;
		}

		var d = dynamicComponents.get(key);

		if (d != null) {
			return d.readComponent(this, reader);
		}

		throw new UnsupportedOperationException("Recipe Component '" + reader.getRemaining() + "' not found");
	}
}
