package dev.latvian.mods.kubejs.recipe.schema;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.RecipeTypeFunction;
import dev.latvian.mods.kubejs.recipe.component.ComponentValueMap;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.rhino.Context;

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RecipeConstructor {
	public final RecipeKey<?>[] keys;
	public Map<RecipeKey<?>, RecipeOptional<?>> overrides;

	public RecipeConstructor(RecipeKey<?>... keys) {
		this.keys = keys;
		this.overrides = Map.of();
	}

	public <T> RecipeConstructor override(RecipeKey<T> key, RecipeOptional<T> value) {
		if (overrides.isEmpty()) {
			overrides = new IdentityHashMap<>(1);
		}

		overrides.put(key, value);
		return this;
	}

	public <T> RecipeConstructor overrideValue(RecipeKey<T> key, T value) {
		return override(key, new RecipeOptional.Constant<>(value));
	}

	public RecipeConstructor overrides(Map<RecipeKey<?>, RecipeOptional<?>> map) {
		overrides = map;
		return this;
	}

	@Override
	public String toString() {
		return Arrays.stream(keys).map(RecipeKey::toString).collect(Collectors.joining(", ", "(", ")"));
	}

	public KubeRecipe create(Context cx, RecipeTypeFunction type, RecipeSchemaType schemaType, ComponentValueMap from) {
		var r = schemaType.schema.recipeFactory.create();
		r.sourceLine = SourceLine.of(cx);
		r.type = type;
		r.json = new JsonObject();
		r.json.addProperty("type", "unknown");
		r.newRecipe = true;
		r.initValues(true);
		setValues(cx, r, schemaType, from);
		return r;
	}

	public void setValues(Context cx, KubeRecipe recipe, RecipeSchemaType schemaType, ComponentValueMap from) {
		for (var key : keys) {
			recipe.setValue(key, Cast.to(from.getValue(cx, recipe, key)));
		}

		for (var entry : overrides.entrySet()) {
			recipe.setValue(entry.getKey(), Cast.to(entry.getValue().getDefaultValue(schemaType)));
		}
	}
}
