package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.desc.DescriptionContext;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
import dev.latvian.mods.kubejs.util.TinyMap;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * A <b>recipe component</b> is a reusable definition of a recipe element (such as an in/output item, a fluid, or even just a number value)
 * that has a {@link #role() role}, a {@link #constructorDescription(DescriptionContext) description} and a {@link #componentClass() value class}
 * associated with it and defines logic on how to {@link #read(KubeRecipe, Object) read} and {@link #write(KubeRecipe, Object) write} the value
 * contained within the context of a recipe.
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
	default RecipeKey<T> key(String name) {
		return new RecipeKey<>(this, name);
	}

	/**
	 * Defines the {@link ComponentRole role} of this component.
	 * <p>
	 * This is used during input / output replacement to determine which components are eligible for replacement,
	 * as well as populating the {@link KubeRecipe#inputValues()} and {@link KubeRecipe#outputValues()} arrays.
	 *
	 * @return The role of this component
	 */
	default ComponentRole role() {
		return ComponentRole.OTHER;
	}

	/**
	 * Defines the string type of this component, mostly used for logging and debugging purposes.
	 * <p>
	 * For a description of how what the component is actually composed of, which may be used by addons like ProbeJS
	 * to describe it, refer to {@link #constructorDescription(DescriptionContext) this method} instead.
	 *
	 * @return The type of this component
	 * @see #constructorDescription(DescriptionContext)
	 */
	default String componentType() {
		return "unknown";
	}

	/**
	 * Defines the class of the value contained within this component.
	 *
	 * @return This component's value class
	 */
	Class<?> componentClass();

	/**
	 * Defines a description for how this component may be constructed.
	 * Type descriptions may be comprised of a primitive type such as a string,
	 * number or Java class (which may be useful if that that class has an appropriate
	 * type wrapper for it already), an array of fixed or dynamic length, a map / object,
	 * or a union of multiple types.
	 * <p>
	 * Type descriptions are used by addons like ProbeJS to provide typing hints.
	 *
	 * @param ctx The description context
	 * @return A description of how this component may be constructed
	 */
	default TypeDescJS constructorDescription(DescriptionContext ctx) {
		return ctx.javaType(componentClass());
	}

	/**
	 * Method to write the value contained within this component to a JSON object.
	 *
	 * @param recipe The recipe object used for context
	 * @param value  The value to write
	 * @return The JSON representation of the written value
	 */
	JsonElement write(KubeRecipe recipe, T value);

	/**
	 * Method to read the value contained within this component from an input object;
	 * this may be a JSON element (if reading from JSON) or some arbitrary value passed
	 * into a {@link RecipeSchema schema's} constructor(s) or automatically generated
	 * builder methods.
	 *
	 * @param recipe The recipe object used for context
	 * @param from   An object to be converted to a value for this component
	 * @return The value read from the input
	 */
	T read(KubeRecipe recipe, Object from);

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

		json.add(cv.key.name, write(recipe, cv.value));
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

	/**
	 * This method serves as a more specialized override for deserializing from a map,
	 * providing the map itself as additional context.
	 *
	 * @param recipe The recipe object used for context
	 * @param cv     The holder object to store the resulting value in
	 * @param map    The map to read from (just like with JSON, this may be a nested map in the case of some components)
	 */
	default void readFromMap(KubeRecipe recipe, RecipeComponentValue<T> cv, Map<?, ?> map) {
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

	/**
	 * Declares whether this component should take priority when being
	 * considered by e.g. an {@link OrRecipeComponent} during deserialization.
	 *
	 * @param recipe The recipe object used for context
	 * @param from   The object to be deserialized from
	 * @return Whether this component should take priority
	 */
	default boolean hasPriority(KubeRecipe recipe, Object from) {
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

	default T replaceInput(KubeRecipe recipe, T original, ReplacementMatch match, InputReplacement with) {
		return original instanceof InputReplacement r && isInput(recipe, original, match) ? read(recipe, with.replaceInput(recipe, match, r)) : original;
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

	default T replaceOutput(KubeRecipe recipe, T original, ReplacementMatch match, OutputReplacement with) {
		return original instanceof OutputReplacement r && isOutput(recipe, original, match) ? read(recipe, with.replaceOutput(recipe, match, r)) : original;
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

	// The below helpers are mainly intended for scripters, if you are an addon developer just make your own component instead of reusing the existing ones.
	@Info("Returns a new RecipeComponent that applies the mappingTo function to the input before it is passed to this component to be read")
	default MappingRecipeComponent<T> mapIn(UnaryOperator<Object> mappingTo) {
		return map(mappingTo, UnaryOperator.identity());
	}

	@Info("Returns a new RecipeComponent that applies the mappingFrom function after the component writes to json, before that json is saved")
	default MappingRecipeComponent<T> mapOut(UnaryOperator<JsonElement> mappingFrom) {
		return map(UnaryOperator.identity(), mappingFrom);
	}

	@Info("Returns a new RecipeComponent that applies the mappingTo function to the input before it is passed to this component to be read, and the mappingFrom function after the component writes to json, before that json is saved")
	default MappingRecipeComponent<T> map(UnaryOperator<Object> mappingTo, UnaryOperator<JsonElement> mappingFrom) {
		return new MappingRecipeComponent<>(this, mappingTo, mappingFrom);
	}

	@Info("""
		Returns a new RecipeComponent that maps the keys in a JsonObject according to the provided map, both before the json gets passed to the component and after the component returns a written json object.
		The mappings should be provided in the format `{recipe: "component"}` where recipe is the key as in the recipe, and component is the key as how the RecipeComponent expects it.
		Any keys not included in the provided map will be ignored, and any keys in the provided map that are not in either the input object or output object will be ignored.
		Note that if the input or output is not a JsonObject (ie its an ItemStack, or it is a JsonPrimitive) then that will pass through this without being modified.
		If you wish to handle those situations use the actual map function""")
	default SimpleMappingRecipeComponent<T> simpleMap(Object mappings) {
		return new SimpleMappingRecipeComponent<>(this, mappings);
	}
}
