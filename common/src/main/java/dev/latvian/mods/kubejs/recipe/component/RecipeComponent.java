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
import java.util.Map;

/**
 * A <b>recipe component</b> is a reusable definition of a recipe element (such as an in/output item, a fluid, or even just a number value)
 * that has a {@link #role() role}, a {@link #constructorDescription(DescriptionContext) description} and a {@link #componentClass() value class}
 * associated with it and defines logic on how to {@link #read(RecipeJS, Object) read} and {@link #write(RecipeJS, Object) write} the value
 * contained within the context of a recipe.
 * <p>
 * Recipe components are used in conjunction with {@link RecipeKey}s to define the structure of a recipe,
 * and are also referred to by bulk recipe operations such as {@link #replaceInput(RecipeJS, Object, ReplacementMatch, InputReplacement) input} and
 * {@link #replaceOutput(RecipeJS, Object, ReplacementMatch, OutputReplacement) output} replacements.
 * <p>
 * There are lots of standard components provided in the {@link dev.latvian.mods.kubejs.recipe.component} package,
 * including items and fluid in- and outputs, generic group and logic components (array, map, and, or)
 * and all kinds of primitives (including specialised ones such as number ranges and characters), which you can use to
 * more easily define standardised components for your own recipes, though you may also want to define your own components
 * from the ground up depending on your use case.
 *
 * @param <T> The value type of this component
 * @see RecipeComponentWithParent
 * @see AndRecipeComponent
 */
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

	default void writeToJson(RecipeJS recipe, RecipeComponentValue<T> cv, JsonObject json) {
		if (cv.key.names.size() >= 2) {
			for (var k : cv.key.names) {
				json.remove(k);
			}
		}

		json.add(cv.key.name, write(recipe, cv.value));
	}

	default void readFromJson(RecipeJS recipe, RecipeComponentValue<T> cv, JsonObject json) {
		var v = json.get(cv.key.name);

		if (v != null) {
			cv.value = read(recipe, v);
		} else if (cv.key.names.size() >= 2) {
			for (var alt : cv.key.names) {
				v = json.get(alt);

				if (v != null) {
					cv.value = read(recipe, v);
					return;
				}
			}
		}
	}

	default void readFromMap(RecipeJS recipe, RecipeComponentValue<T> cv, Map<?, ?> map) {
		var v = map.get(cv.key.name);

		if (v != null) {
			cv.value = read(recipe, v);
		} else if (cv.key.names.size() >= 2) {
			for (var alt : cv.key.names) {
				v = map.get(alt);

				if (v != null) {
					cv.value = read(recipe, v);
					return;
				}
			}
		}
	}

	default boolean hasPriority(RecipeJS recipe, Object from) {
		return false;
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

	default boolean checkValueHasChanged(T oldValue, T newValue) {
		return oldValue != newValue;
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
