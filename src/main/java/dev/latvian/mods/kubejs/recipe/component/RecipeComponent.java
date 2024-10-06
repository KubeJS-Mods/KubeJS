package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.kubejs.error.EmptyRecipeComponentValueException;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.RecipesKubeEvent;
import dev.latvian.mods.kubejs.recipe.match.Replaceable;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.util.TinyMap;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A <b>recipe component</b> is a reusable definition of a recipe element (such as an in/output item, a fluid, or even just a number value)
 * that has a {@link #typeInfo() description} associated with it and defines logic on how to serialize the value
 * contained within the context of a recipe with a {@link #codec()}.
 * <p>
 * Recipe components are used in conjunction with {@link RecipeKey}s to define the structure of a recipe,
 * and are also referred to by bulk recipe operations such as replacements.
 * <p>
 * There are lots of standard components provided in the {@link dev.latvian.mods.kubejs.recipe.component} package,
 * including items and fluid in- and outputs, generic group and logic components (array, map, and, or)
 * and all kinds of primitives (including specialised ones such as number ranges and characters), which you can use to
 * more easily define standardised components for your own recipes, though you may also want to define your own components
 * from the ground up depending on your use case.
 *
 * @param <T> The value type of this component
 * @see RecipeComponentWithParent
 * @see PairRecipeComponent
 */
@Nullable
public interface RecipeComponent<T> {
	static RecipeComponentBuilder builder(List<RecipeComponentBuilder.Key> keys) {
		return new RecipeComponentBuilder(keys);
	}

	static RecipeComponentBuilder builder(RecipeComponentBuilder.Key... keys) {
		return new RecipeComponentBuilder(List.of(keys));
	}

	/**
	 * Creates a new {@link RecipeKey} for this component with the given name.
	 *
	 * @param name The name of the key
	 * @return The created {@link RecipeKey}
	 */
	default RecipeKey<T> key(String name, ComponentRole role) {
		return new RecipeKey<>(this, name, role);
	}

	default RecipeKey<T> inputKey(String name) {
		return key(name, ComponentRole.INPUT);
	}

	default RecipeKey<T> outputKey(String name) {
		return key(name, ComponentRole.OUTPUT);
	}

	default RecipeKey<T> otherKey(String name) {
		return key(name, ComponentRole.OTHER);
	}

	Codec<T> codec();

	/**
	 * Defines a description for how this component may be constructed.
	 * Type descriptions may be comprised of a primitive type such as a string,
	 * number or Java class (which may be useful if that that class has an appropriate
	 * type wrapper for it already), an array of fixed or dynamic length, a map / object,
	 * or a union of multiple types.
	 * <p>
	 * Type descriptions are used by addons like ProbeJS to provide typing hints.
	 *
	 * @return A description of how this component may be constructed
	 */
	TypeInfo typeInfo();

	/**
	 * Method to read the value contained within this component from an input object;
	 * this may be some arbitrary value passed into a {@link RecipeSchema schema's}
	 * constructor(s) or automatically generated builder methods. By default, it will
	 * attempt to type wrap based on {@link #typeInfo()}
	 *
	 * @param cx     JavaScript context
	 * @param recipe The recipe object used for context
	 * @param from   An object to be converted to a value for this component
	 * @return The value read from the input
	 */
	default T wrap(Context cx, KubeRecipe recipe, Object from) {
		return (T) cx.jsToJava(from, typeInfo());
	}

	/**
	 * This method serves as a more specialized override for serializing to JSON,
	 * providing the JSON object as additional context.
	 *
	 * @param recipe The recipe object used for context
	 * @param cv     A holder object to retrieve the component's value from
	 * @param json   The root JSON object to write to
	 *               (this might be the root of the recipe JSON, or a nested object inside if
	 *               this component is contained within for example a RecipeComponentBuilder)
	 */
	default void writeToJson(KubeRecipe recipe, RecipeComponentValue<T> cv, JsonObject json) {
		if (cv.key.names.size() >= 2) {
			for (var k : cv.key.names) {
				json.remove(k);
			}
		}

		switch (cv.key.codec.encodeStart(recipe.type.event.registries.json(), cv.value)) {
			case DataResult.Success(var value, var lifecycle) -> json.add(cv.key.name, value);
			case DataResult.Error<JsonElement> error -> ConsoleJS.SERVER.error("Failed to encode " + cv.key.name + " for recipe " + recipe.id + " from value" + cv.value + ": " + error.message(), recipe.sourceLine, null, RecipesKubeEvent.POST_SKIP_ERROR);
		}
	}

	/**
	 * This method serves as a more specialized override for deserializing from JSON,
	 * providing the JSON object as additional context.
	 *
	 * @param recipe The recipe object used for context
	 * @param cv     The holder object to store the resulting value in
	 * @param json   The root JSON object to read from
	 *               (this might be the root of the recipe JSON, or a nested object inside if
	 *               this component is contained within for example a RecipeComponentBuilder)
	 */
	default void readFromJson(KubeRecipe recipe, RecipeComponentValue<T> cv, JsonObject json) {
		var v = json.get(cv.key.name);

		if (v != null) {
			cv.value = recipe.type.event.registries.decodeJson(cv.key.codec, v);
		} else if (cv.key.names.size() >= 2) {
			for (var alt : cv.key.names) {
				v = json.get(alt);

				if (v != null) {
					cv.value = recipe.type.event.registries.decodeJson(cv.key.codec, v);
					return;
				}
			}
		}
	}

	/**
	 * Declares whether this component should take priority when being
	 * considered by e.g. an {@link EitherRecipeComponent} during deserialization.
	 *
	 * @param recipe The recipe object used for context
	 * @param from   The object to be deserialized from
	 * @return Whether this component should take priority
	 */
	default boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
		return false;
	}

	/**
	 * @param recipe The recipe object used for context
	 * @param value  The value to check
	 * @param match  The replacement match to check against
	 * @return true if the given value matches the given replacement match.
	 */
	default boolean matches(Context cx, KubeRecipe recipe, T value, ReplacementMatchInfo match) {
		return false;
	}

	default T replace(Context cx, KubeRecipe recipe, T original, ReplacementMatchInfo match, Object with) {
		return original instanceof Replaceable r && matches(cx, recipe, original, match) ? wrap(cx, recipe, r.replaceThisWith(cx, with)) : original;
	}

	default void validate(T value) {
		if (isEmpty(value)) {
			throw new EmptyRecipeComponentValueException(this);
		}
	}

	default boolean isEmpty(T value) {
		return false;
	}

	default void buildUniqueId(UniqueIdBuilder builder, T value) {
		builder.append(value.toString());
	}

	default RecipeComponent<List<T>> asList() {
		return ListRecipeComponent.create(this, false, false);
	}

	default RecipeComponent<List<T>> asListOrSelf() {
		return ListRecipeComponent.create(this, true, false);
	}

	default RecipeComponent<List<T>> asConditionalList() {
		return ListRecipeComponent.create(this, false, true);
	}

	default RecipeComponent<List<T>> asConditionalListOrSelf() {
		return ListRecipeComponent.create(this, true, true);
	}

	default RecipeComponent<T> orSelf() {
		return this;
	}

	default <K> RecipeComponent<TinyMap<K, T>> asMap(RecipeComponent<K> key) {
		return new MapRecipeComponent<>(key, this, false);
	}

	default RecipeComponent<TinyMap<Character, T>> asPatternKey() {
		return new MapRecipeComponent<>(CharacterComponent.CHARACTER, this, true);
	}

	default <O> EitherRecipeComponent<T, O> or(RecipeComponent<O> other) {
		return new EitherRecipeComponent<>(this, other);
	}

	default <O> PairRecipeComponent<T, O> and(RecipeComponent<O> other) {
		return new PairRecipeComponent<>(this, other);
	}

	default RecipeComponent<T> withCodec(Codec<T> codec) {
		return new RecipeComponentWithCodec<>(this, codec);
	}
}
