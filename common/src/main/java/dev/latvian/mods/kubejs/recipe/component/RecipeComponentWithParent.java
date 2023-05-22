package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.IngredientMatch;
import dev.latvian.mods.kubejs.recipe.InputItemTransformer;
import dev.latvian.mods.kubejs.recipe.OutputItemTransformer;
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
	default boolean hasInput(T value, IngredientMatch match) {
		return parentComponent().hasInput(value, match);
	}

	@Override
	default T replaceInput(T value, IngredientMatch match, InputItem with, InputItemTransformer transformer, MutableBoolean changed) {
		return parentComponent().replaceInput(value, match, with, transformer, changed);
	}

	@Override
	default boolean hasOutput(T value, IngredientMatch match) {
		return parentComponent().hasOutput(value, match);
	}

	@Override
	default T replaceOutput(T value, IngredientMatch match, OutputItem with, OutputItemTransformer transformer, MutableBoolean changed) {
		return parentComponent().replaceOutput(value, match, with, transformer, changed);
	}
}
