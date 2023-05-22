package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.util.MutableBoolean;
import org.jetbrains.annotations.Nullable;

public interface RecipeComponentWithParent<T> extends RecipeComponent<T> {
	RecipeComponent<T> parentComponent();

	@Override
	default RecipeComponentType getType() {
		return parentComponent().getType();
	}

	@Override
	default String componentType() {
		return parentComponent().componentType();
	}

	@Override
	default JsonObject description() {
		return parentComponent().description();
	}

	@Override
	@Nullable
	default JsonElement write(T value) {
		return parentComponent().write(value);
	}

	@Override
	default T read(Object from) {
		return parentComponent().read(from);
	}

	@Override
	default boolean shouldRead(Object from) {
		return parentComponent().shouldRead(from);
	}

	@Override
	default boolean hasInput(RecipeKJS recipe, T value, ReplacementMatch match) {
		return parentComponent().hasInput(recipe, value, match);
	}

	@Override
	default T replaceInput(RecipeKJS recipe, T value, ReplacementMatch match, InputReplacement with, MutableBoolean changed) {
		return parentComponent().replaceInput(recipe, value, match, with, changed);
	}

	@Override
	default boolean hasOutput(RecipeKJS recipe, T value, ReplacementMatch match) {
		return parentComponent().hasOutput(recipe, value, match);
	}

	@Override
	default T replaceOutput(RecipeKJS recipe, T value, ReplacementMatch match, OutputReplacement with, MutableBoolean changed) {
		return parentComponent().replaceOutput(recipe, value, match, with, changed);
	}
}
