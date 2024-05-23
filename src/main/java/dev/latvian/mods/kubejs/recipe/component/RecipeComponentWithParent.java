package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.typings.desc.DescriptionContext;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
import org.jetbrains.annotations.Nullable;

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
	default ComponentRole role() {
		return parentComponent().role();
	}

	@Override
	default String componentType() {
		return parentComponent().componentType();
	}

	@Override
	default Class<?> componentClass() {
		return parentComponent().componentClass();
	}

	@Override
	default TypeDescJS constructorDescription(DescriptionContext ctx) {
		return parentComponent().constructorDescription(ctx);
	}

	@Override
	@Nullable
	default JsonElement write(KubeRecipe recipe, T value) {
		return parentComponent().write(recipe, value);
	}

	@Override
	default T read(KubeRecipe recipe, Object from) {
		return parentComponent().read(recipe, from);
	}

	@Override
	default boolean hasPriority(KubeRecipe recipe, Object from) {
		return parentComponent().hasPriority(recipe, from);
	}

	@Override
	default boolean isInput(KubeRecipe recipe, T value, ReplacementMatch match) {
		return parentComponent().isInput(recipe, value, match);
	}

	@Override
	default T replaceInput(KubeRecipe recipe, T original, ReplacementMatch match, InputReplacement with) {
		return parentComponent().replaceInput(recipe, original, match, with);
	}

	@Override
	default boolean isOutput(KubeRecipe recipe, T value, ReplacementMatch match) {
		return parentComponent().isOutput(recipe, value, match);
	}

	@Override
	default T replaceOutput(KubeRecipe recipe, T original, ReplacementMatch match, OutputReplacement with) {
		return parentComponent().replaceOutput(recipe, original, match, with);
	}

	@Override
	default String checkEmpty(RecipeKey<T> key, T value) {
		return parentComponent().checkEmpty(key, value);
	}
}
