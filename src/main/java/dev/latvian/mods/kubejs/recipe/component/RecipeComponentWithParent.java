package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.ErrorStack;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A recipe component that may delegate most of its logic to a parent component.
 * A common example of using this would be to define a custom item output component
 * with different serialization or matching logic, but that can still use the same
 * base ItemOutput type as its parent.
 *
 * @param <T> The value type of this component
 */
public interface RecipeComponentWithParent<T> extends RecipeComponent<T> {
	RecipeComponent<T> parentComponent();

	@Override
	default Codec<T> codec() {
		return parentComponent().codec();
	}

	@Override
	default TypeInfo typeInfo() {
		return parentComponent().typeInfo();
	}

	@Override
	default T wrap(Context cx, KubeRecipe recipe, Object from) {
		return parentComponent().wrap(cx, recipe, from);
	}

	@Override
	default boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
		return parentComponent().hasPriority(cx, recipe, from);
	}

	@Override
	default boolean matches(Context cx, KubeRecipe recipe, T value, ReplacementMatchInfo match) {
		return parentComponent().matches(cx, recipe, value, match);
	}

	@Override
	default T replace(Context cx, KubeRecipe recipe, T original, ReplacementMatchInfo match, Object with) {
		return parentComponent().replace(cx, recipe, original, match, with);
	}

	@Override
	default boolean allowEmpty() {
		return parentComponent().allowEmpty();
	}

	@Override
	default void validate(ErrorStack stack, T value) {
		parentComponent().validate(stack, value);
	}

	@Override
	default boolean isEmpty(T value) {
		return parentComponent().isEmpty(value);
	}

	@Override
	default void buildUniqueId(UniqueIdBuilder builder, T value) {
		parentComponent().buildUniqueId(builder, value);
	}

	@Override
	@Nullable
	default RecipeComponentBuilder createBuilder() {
		return parentComponent().createBuilder();
	}

	@Override
	default List<?> spread(T value) {
		return parentComponent().spread(value);
	}
}
