package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.util.MutableBoolean;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

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

	default RecipeComponentType getType() {
		return RecipeComponentType.OTHER;
	}

	default String componentType() {
		return "unknown";
	}

	default JsonObject description() {
		var obj = new JsonObject();
		obj.addProperty("type", componentType());
		return obj;
	}

	@Nullable
	JsonElement write(T value);

	T read(Object from);

	default boolean shouldRead(Object from) {
		return true;
	}

	default boolean hasInput(RecipeKJS recipe, T value, ReplacementMatch match) {
		return false;
	}

	default T replaceInput(RecipeKJS recipe, T value, ReplacementMatch match, InputReplacement with, MutableBoolean changed) {
		return value;
	}

	default boolean hasOutput(RecipeKJS recipe, T value, ReplacementMatch match) {
		return false;
	}

	default T replaceOutput(RecipeKJS recipe, T value, ReplacementMatch match, OutputReplacement with, MutableBoolean changed) {
		return value;
	}

	default RecipeComponent<List<T>> asArray() {
		return new ArrayRecipeComponent<>(this, false);
	}

	default RecipeComponent<List<T>> asArrayOrSelf() {
		return new ArrayRecipeComponent<>(this, true);
	}

	default <K> RecipeComponent<Map<K, T>> asMap(RecipeComponent<K> key) {
		return new MapRecipeComponent<>(key, this);
	}

	default RecipeComponent<Map<Character, T>> asPatternKey() {
		return asMap(StringComponent.CHARACTER);
	}

	default RecipeComponent<T> optional(T defaultValue) {
		return new OptionalRecipeComponent<>(this, defaultValue, false);
	}

	default RecipeComponent<T> optionalAlwaysWrite(T defaultValue) {
		return new OptionalRecipeComponent<>(this, defaultValue, true);
	}

	default <R extends RecipeJS> RecipeKey<T> key(int index, String name) {
		return new RecipeKey<>(this, index, name);
	}
}
