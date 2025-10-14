package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.kubejs.error.EmptyRecipeComponentException;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.RecipesKubeEvent;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.Replaceable;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.util.IntBounds;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.kubejs.util.TinyMap;
import dev.latvian.mods.rhino.type.TypeInfo;
import org.jetbrains.annotations.ApiStatus;
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
 */
public interface RecipeComponent<T> {
	static CustomObjectRecipeComponent builder(List<CustomObjectRecipeComponent.Key> keys) {
		return new CustomObjectRecipeComponent(keys);
	}

	static CustomObjectRecipeComponent builder(CustomObjectRecipeComponent.Key... keys) {
		return new CustomObjectRecipeComponent(List.of(keys));
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

	RecipeComponentType<?> type();

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
	 * Declares whether this component should take priority when being
	 * considered by e.g. an {@link EitherRecipeComponent} during deserialization.
	 *
	 * @param cx   Script context
	 * @param from The object to be deserialized from
	 * @return Whether this component should take priority
	 */
	default boolean hasPriority(RecipeMatchContext cx, Object from) {
		return false;
	}

	/**
	 * Method to read the value contained within this component from an input object;
	 * this may be some arbitrary value passed into a {@link RecipeSchema schema's}
	 * constructor(s) or automatically generated builder methods. By default, it will
	 * attempt to type wrap based on {@link #typeInfo()}
	 *
	 * @param cx   Script context
	 * @param from An object to be converted to a value for this component
	 * @return The value read from the input
	 */
	default T wrap(RecipeScriptContext cx, Object from) {
		return (T) cx.cx().jsToJava(from, typeInfo());
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

		try {
			var result = cv.key.codec.encodeStart(recipe.type.event.ops.json(), cv.value);

			switch (result) {
				case DataResult.Success<JsonElement> r -> json.add(cv.key.name, r.value());
				case DataResult.Error<JsonElement> r -> ConsoleJS.SERVER.error("Failed to encode " + cv.key.name + " for recipe " + recipe.id + " from value" + cv.value + ": " + r.message(), recipe.sourceLine, null, RecipesKubeEvent.POST_SKIP_ERROR);
			}
		} catch (Exception ex) {
			ConsoleJS.SERVER.error("Failed to encode " + cv.key.name + " for recipe " + recipe.id + " from value" + cv.value + ": " + ex, recipe.sourceLine, ex, RecipesKubeEvent.POST_SKIP_ERROR);
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
			cv.value = cv.key.codec.parse(recipe.type.event.ops.json(), v).getOrThrow();
		} else if (cv.key.names.size() >= 2) {
			for (var alt : cv.key.names) {
				v = json.get(alt);

				if (v != null) {
					cv.value = cv.key.codec.parse(recipe.type.event.ops.json(), v).getOrThrow();
					return;
				}
			}
		}
	}

	/**
	 * @param cx    Script context
	 * @param value The value to check
	 * @param match The replacement match to check against
	 * @return true if the given value matches the given replacement match.
	 */
	default boolean matches(RecipeMatchContext cx, T value, ReplacementMatchInfo match) {
		return false;
	}

	default T replace(RecipeScriptContext cx, T original, ReplacementMatchInfo match, Object with) {
		return original instanceof Replaceable r && matches(cx, original, match) ? this.wrap(cx, r.replaceThisWith(cx, with)) : original;
	}

	default boolean allowEmpty() {
		return false;
	}

	default void validate(RecipeValidationContext ctx, T value) {
		if (!allowEmpty() && isEmpty(value)) {
			throw new EmptyRecipeComponentException(this, value);
		}
	}

	/**
	 * Shallow empty check function
	 */
	default boolean isEmpty(T value) {
		return false;
	}

	default void buildUniqueId(UniqueIdBuilder builder, T value) {
		builder.append(value.toString());
	}

	default String toString(OpsContainer ops, T value) {
		return value.toString();
	}

	default ListRecipeComponent<T> asList() {
		return ListRecipeComponent.create(this, false, false);
	}

	default ListRecipeComponent<T> asListOrSelf() {
		return ListRecipeComponent.create(this, true, false);
	}

	default ListRecipeComponent<T> asConditionalList() {
		return ListRecipeComponent.create(this, false, true);
	}

	default ListRecipeComponent<T> asConditionalListOrSelf() {
		return ListRecipeComponent.create(this, true, true);
	}

	default RecipeComponent<T> orSelf() {
		return this;
	}

	default <K> RecipeComponent<TinyMap<K, T>> asMap(RecipeComponent<K> key) {
		return MapRecipeComponent.of(key, this, IntBounds.DEFAULT);
	}

	default RecipeComponent<TinyMap<Character, T>> asPatternKey() {
		return MapRecipeComponent.patternOf(this, IntBounds.DEFAULT);
	}

	default <O> EitherRecipeComponent<T, O> or(RecipeComponent<O> other) {
		return new EitherRecipeComponent<>(this, other);
	}

	default RecipeComponent<T> withCodec(Codec<T> codec) {
		return new RecipeComponentWithCodec<>(this, codec);
	}

	@Nullable
	@ApiStatus.Experimental
	default RecipeComponentBuilder createBuilder() {
		return null;
	}

	default List<?> spread(T value) {
		return List.of(value);
	}

	default boolean isIgnored() {
		return false;
	}
}
