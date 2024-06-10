package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.util.TinyMap;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import org.jetbrains.annotations.Nullable;

/**
 * A <b>recipe component</b> is a reusable definition of a recipe element (such as an in/output item, a fluid, or even just a number value)
 * that has a {@link #typeInfo() description} associated with it and defines logic on how to serialize the value
 * contained within the context of a recipe with a {@link #codec()}.
 * <p>
 * Recipe components are used in conjunction with {@link RecipeKey}s to define the structure of a recipe,
 * and are also referred to by bulk recipe operations such as {@link #replaceInput(KubeRecipe, Object, ReplacementMatch, InputReplacement) input} and
 * {@link #replaceOutput(KubeRecipe, Object, ReplacementMatch, OutputReplacement) output} replacements.
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
	Codec<RecipeComponent<?>> CODEC = Codec.unit(null); // FIXME!!!

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

		json.add(cv.key.name, cv.key.codec.encodeStart(JsonOps.INSTANCE, cv.value).getOrThrow());
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
			cv.value = cv.key.codec.decode(JsonOps.INSTANCE, v).getOrThrow().getFirst();
		} else if (cv.key.names.size() >= 2) {
			for (var alt : cv.key.names) {
				v = json.get(alt);

				if (v != null) {
					cv.value = cv.key.codec.decode(JsonOps.INSTANCE, v).getOrThrow().getFirst();
					return;
				}
			}
		}
	}

	/**
	 * Declares whether this component should take priority when being
	 * considered by e.g. an {@link OrRecipeComponent} during deserialization.
	 *
	 * @param recipe The recipe object used for context
	 * @param from   The object to be deserialized from
	 * @return Whether this component should take priority
	 */
	default boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
		return false;
	}

	/**
	 * Returns true if the given value is considered a valid input for this component
	 * that matches the given replacement match.
	 *
	 * @param recipe The recipe object used for context
	 * @param value  The value to check
	 * @param match  The replacement match to check against
	 * @return Whether the given value is a matched input for this component
	 */
	default boolean isInput(KubeRecipe recipe, T value, ReplacementMatch match) {
		return false;
	}

	default T replaceInput(Context cx, KubeRecipe recipe, T original, ReplacementMatch match, InputReplacement with) {
		return original instanceof InputReplacement r && isInput(recipe, original, match) ? wrap(cx, recipe, with.replaceInput(cx, recipe, match, r)) : original;
	}

	/**
	 * Returns true if the given value is considered a valid output for this component
	 * that matches the given replacement match.
	 *
	 * @param recipe The recipe object used for context
	 * @param value  The value to check
	 * @param match  The replacement match to check against
	 * @return Whether the given value is a matched output for this component
	 */
	default boolean isOutput(KubeRecipe recipe, T value, ReplacementMatch match) {
		return false;
	}

	default T replaceOutput(Context cx, KubeRecipe recipe, T original, ReplacementMatch match, OutputReplacement with) {
		return original instanceof OutputReplacement r && isOutput(recipe, original, match) ? wrap(cx, recipe, with.replaceOutput(cx, recipe, match, r)) : original;
	}

	/**
	 * This method may be used by some components to validate that the value read
	 * from the input is valid / not empty. If the value is empty, this method should
	 * return a string describing the error, otherwise it should return an empty string.
	 *
	 * @param key   The key of the component that was read
	 * @param value The value read from the input
	 * @return An error message, or an empty string if the value is valid
	 */
	default String checkEmpty(RecipeKey<T> key, T value) {
		return "";
	}

	default boolean checkValueHasChanged(T oldValue, T newValue) {
		return oldValue != newValue;
	}

	@Nullable
	default String createUniqueId(T value) {
		return value == null ? null : value.toString().toLowerCase().replaceAll("\\W", "_").replaceAll("_{2,}", "_");
	}

	default ListRecipeComponent<T> asList() {
		return ListRecipeComponent.create(this, false);
	}

	default ListRecipeComponent<T> asListOrSelf() {
		return ListRecipeComponent.create(this, true);
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

	default <O> OrRecipeComponent<T, O> or(RecipeComponent<O> other) {
		return new OrRecipeComponent<>(this, other);
	}

	default <O> AndRecipeComponent<T, O> and(RecipeComponent<O> other) {
		return new AndRecipeComponent<>(this, other);
	}

	default RecipeComponent<T> withCodec(Codec<T> codec) {
		return new RecipeComponentWithCodec<>(this, codec);
	}
}
