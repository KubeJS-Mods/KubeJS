package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.typings.desc.DescriptionContext;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
import dev.latvian.mods.kubejs.util.TinyMap;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;

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

	default TypeDescJS constructorDescription(DescriptionContext ctx) {
		return ctx.javaType(componentClass());
	}

	JsonElement write(RecipeJS recipe, T value);

	T read(RecipeJS recipe, Object from);

	default void writeToJson(RecipeComponentValue<T> value, JsonObject json) {
		if (value.key.names.size() >= 2) {
			for (var k : value.key.names) {
				json.remove(k);
			}
		}

		json.add(value.key.name, write(value.recipe, value.value));
	}

	@Nullable
	default T readFromJson(RecipeJS recipe, RecipeKey<T> key, JsonObject json) {
		var v = json.get(key.name);

		if (v != null) {
			return read(recipe, v);
		} else if (key.names.size() >= 2) {
			for (var alt : key.names) {
				v = json.get(alt);

				if (v != null) {
					return read(recipe, v);
				}
			}
		}

		return null;
	}

	default boolean hasPriority(RecipeJS recipe, Object from) {
		return true;
	}

	default boolean isInput(RecipeJS recipe, T value, ReplacementMatch match) {
		return false;
	}

	default T replaceInput(RecipeJS recipe, T original, ReplacementMatch match, InputReplacement with) {
		return original instanceof InputReplacement r && isInput(recipe, original, match) ? read(recipe, with.replaceInput(recipe, match, r)) : original;
	}

	default boolean isOutput(RecipeJS recipe, T value, ReplacementMatch match) {
		return false;
	}

	default T replaceOutput(RecipeJS recipe, T original, ReplacementMatch match, OutputReplacement with) {
		return original instanceof OutputReplacement r && isOutput(recipe, original, match) ? read(recipe, with.replaceOutput(recipe, match, r)) : original;
	}

	default String checkEmpty(RecipeKey<T> key, T value) {
		return "";
	}

	@SuppressWarnings("unchecked")
	default ArrayRecipeComponent<T> asArray() {
		var arr = (T[]) Array.newInstance(componentClass(), 0);
		return new ArrayRecipeComponent<>(this, false, arr.getClass(), arr);
	}

	@SuppressWarnings("unchecked")
	default ArrayRecipeComponent<T> asArrayOrSelf() {
		var arr = (T[]) Array.newInstance(componentClass(), 0);
		return new ArrayRecipeComponent<>(this, true, arr.getClass(), arr);
	}

	default RecipeComponent<T> orSelf() {
		return this;
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
