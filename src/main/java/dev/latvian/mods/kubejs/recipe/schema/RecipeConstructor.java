package dev.latvian.mods.kubejs.recipe.schema;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.RecipeTypeFunction;
import dev.latvian.mods.kubejs.recipe.component.ComponentValueMap;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.ErrorStack;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import it.unimi.dsi.fastutil.objects.Reference2ObjectLinkedOpenHashMap;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecipeConstructor {
	public final List<RecipeKey<?>> keys;
	public Map<RecipeKey<?>, RecipeOptional<?>> overrides;
	public Map<RecipeKey<?>, RecipeOptional<?>> defaultValues;

	public RecipeConstructor(List<RecipeKey<?>> keys) {
		this.keys = keys;
		this.overrides = Map.of();
		this.defaultValues = Map.of();
	}

	public RecipeConstructor(RecipeKey<?>... keys) {
		this(List.of(keys));
	}

	public <T> RecipeConstructor override(RecipeKey<T> key, RecipeOptional<T> value) {
		if (overrides.isEmpty()) {
			overrides = new Reference2ObjectLinkedOpenHashMap<>(1);
		}

		overrides.put(key, value);
		return this;
	}

	public <T> RecipeConstructor overrideValue(RecipeKey<T> key, T value) {
		return override(key, RecipeOptional.unit(value));
	}

	public <T> RecipeConstructor defaultValue(RecipeKey<T> key, RecipeOptional<T> value) {
		if (defaultValues.isEmpty()) {
			defaultValues = new Reference2ObjectLinkedOpenHashMap<>(1);
		}

		defaultValues.put(key, value);
		return this;
	}

	@Override
	public String toString() {
		return toString(RegistryAccessContainer.current);
	}

	public String toString(OpsContainer ops) {
		var str = keys.stream().map(RecipeKey::toString).collect(Collectors.joining(", ", "(", ")"));

		if (!overrides.isEmpty() || !defaultValues.isEmpty()) {
			var map = new LinkedHashMap<RecipeKey<?>, RecipeOptional<?>>();
			map.putAll(defaultValues);
			map.putAll(overrides);

			str += map.entrySet().stream().map(e -> {
				var k = e.getKey();

				try {
					var v = e.getValue().getInformativeValue();

					if (v == null) {
						return k.name + " = ?";
					} else {
						return k.name + " = " + k.component.toString(ops, Cast.to(v));
					}
				} catch (Throwable ex) {
					return k.name + " = ?";
				}
			}).collect(Collectors.joining(", ", " [", "]"));
		}

		return str;
	}

	public KubeRecipe create(Context cx, SourceLine sourceLine, RecipeTypeFunction type, RecipeSchemaType schemaType, ComponentValueMap from) {
		var recipe = schemaType.schema.recipeFactory.create(type, sourceLine, true);
		recipe.json = new JsonObject();
		recipe.json.addProperty("type", type.idString);
		recipe.newRecipe = true;
		setValues(new RecipeScriptContext.Impl(cx, recipe, new ErrorStack()), schemaType, from);
		return recipe;
	}

	public void setValues(RecipeScriptContext cx, RecipeSchemaType schemaType, ComponentValueMap from) {
		var recipe = cx.recipe();
		cx.errors().push("keys");

		for (var key : keys) {
			cx.errors().setKey(key.name);
			recipe.setValue(key, Cast.to(from.getValue(cx, key)));
		}

		cx.errors().pop();
		cx.errors().push("overrides");

		for (var entry : overrides.entrySet()) {
			cx.errors().setKey(entry.getKey().name);
			recipe.setValue(entry.getKey(), Cast.to(entry.getValue().getDefaultValue(schemaType)));
		}

		cx.errors().pop();
		cx.errors().push("key_overrides");

		for (var entry : schemaType.schema.keyOverrides.entrySet()) {
			cx.errors().setKey(entry.getKey().name);
			recipe.setValue(entry.getKey(), Cast.to(entry.getValue().getDefaultValue(schemaType)));
		}

		cx.errors().pop();
	}

	public JsonObject toJson(RecipeSchemaType type, DynamicOps<JsonElement> ops) {
		var json = new JsonObject();

		var k = new JsonArray(keys.size());

		for (var key : keys) {
			k.add(key.name);
		}

		json.add("keys", k);

		if (!overrides.isEmpty()) {
			var o = new JsonObject();

			for (var entry : overrides.entrySet()) {
				o.add(entry.getKey().name, entry.getKey().codec.encodeStart(ops, Cast.to(entry.getValue().getDefaultValue(type))).getOrThrow());
			}

			json.add("overrides", o);
		}

		return json;
	}
}
