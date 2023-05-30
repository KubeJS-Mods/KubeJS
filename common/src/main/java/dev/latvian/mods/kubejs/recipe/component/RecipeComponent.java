package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.util.TinyMap;
import org.jetbrains.annotations.Nullable;

@Nullable
public interface RecipeComponent<T> {
	static RecipeComponentBuilder builder() {
		return new RecipeComponentBuilder(4);
	}

	static RecipeComponentBuilder builder(RecipeKey<?>... key) {
		var b = new RecipeComponentBuilder(key.length);

		for (var k : key) {
			b.add(k);
		}

		return b;
	}

	default RecipeKey<T> key(String name) {
		return new RecipeKey<>(this, name);
	}

	default ComponentRole role() {
		return ComponentRole.OTHER;
	}

	default String componentType() {
		return "unknown";
	}

	Class<?> componentClass();

	default JsonElement description(RecipeJS recipe) {
		return new JsonPrimitive(componentType());
	}

	JsonElement write(RecipeJS recipe, T value);

	T read(RecipeJS recipe, Object from);

	default void writeJson(RecipeComponentValue<T> value, JsonObject json) {
		if (value.key.names().size() >= 2) {
			for (var k : value.key.names()) {
				json.remove(k);
			}
		}

		json.add(value.key.name(), write(value.recipe, value.value));
	}

	default void readJson(RecipeComponentValue<T> value, JsonObject json) {
		var v = json.get(value.key.name());

		if (v != null) {
			value.value = read(value.recipe, v);
			return;
		} else if (value.key.names().size() >= 2) {
			for (var alt : value.key.names()) {
				v = json.get(alt);

				if (v != null) {
					value.value = read(value.recipe, v);
					return;
				}
			}
		}

		throw new MissingComponentException(value.key);
	}

	default boolean hasPriority(RecipeJS recipe, Object from) {
		return true;
	}

	default boolean isInput(RecipeJS recipe, T value, ReplacementMatch match) {
		return false;
	}

	default T replaceInput(RecipeJS recipe, T value, ReplacementMatch match, InputReplacement with) {
		return value;
	}

	default boolean isOutput(RecipeJS recipe, T value, ReplacementMatch match) {
		return false;
	}

	default T replaceOutput(RecipeJS recipe, T value, ReplacementMatch match, OutputReplacement with) {
		return value;
	}

	default String checkEmpty(RecipeKey<T> key, T value) {
		return "";
	}

	default RecipeComponent<T[]> asArray() {
		return ArrayRecipeComponent.of(this, false);
	}

	default RecipeComponent<T[]> asArrayOrSelf() {
		return ArrayRecipeComponent.of(this, true);
	}

	default <K> RecipeComponent<TinyMap<K, T>> asMap(RecipeComponent<K> key) {
		return new MapRecipeComponent<>(key, this, false);
	}

	default RecipeComponent<TinyMap<Character, T>> asPatternKey() {
		return new MapRecipeComponent<>(StringComponent.CHARACTER, this, true);
	}

	default <O> OrRecipeComponent<T, O> or(RecipeComponent<O> other) {
		return new OrRecipeComponent<>(this, other);
	}

	default <O> AndRecipeComponent<T, O> and(RecipeComponent<O> other) {
		return new AndRecipeComponent<>(this, other);
	}
}
