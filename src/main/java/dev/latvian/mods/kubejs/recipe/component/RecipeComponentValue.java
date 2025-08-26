package dev.latvian.mods.kubejs.recipe.component;

import dev.latvian.mods.kubejs.error.InvalidRecipeComponentException;
import dev.latvian.mods.kubejs.error.MissingRequiredValueException;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.WrappedJS;
import dev.latvian.mods.rhino.Context;

import java.util.Map;
import java.util.Objects;

public final class RecipeComponentValue<T> implements WrappedJS, Map.Entry<RecipeKey<T>, T> {
	public static final RecipeComponentValue<?>[] EMPTY_ARRAY = new RecipeComponentValue[0];

	public final RecipeKey<T> key;
	public final int index;
	public T value;
	public boolean write;

	public RecipeComponentValue(RecipeKey<T> key, int index) {
		this.key = key;
		this.index = index;
		this.value = null;
		this.write = false;
	}

	public RecipeComponentValue<T> copy() {
		var copy = new RecipeComponentValue<>(key, index);
		copy.value = value;
		copy.write = write;
		return copy;
	}

	public boolean matches(Context cx, KubeRecipe recipe, ReplacementMatchInfo match) {
		return value != null && (match.componentType().isEmpty() || key.component.equals(match.componentType().get())) && key.component.matches(cx, recipe, value, match);
	}

	public boolean replace(Context cx, KubeRecipe recipe, ReplacementMatchInfo match, Object with) {
		var newValue = value == null ? null : key.component.replace(cx, recipe, value, match, with);

		if (value != newValue) {
			value = newValue;
			write();
			return true;
		}

		return false;
	}

	@Override
	public RecipeKey<T> getKey() {
		return key;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public T getValue() {
		return value;
	}

	@Override
	public T setValue(T newValue) {
		var v = value;
		value = newValue;
		return v;
	}

	public boolean shouldWrite() {
		return write;
	}

	public void write() {
		write = true;
	}

	@Override
	public String toString() {
		return "%s = %s".formatted(key.name, value);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof Map.Entry<?, ?> e && key == e.getKey() && Objects.equals(value, e.getValue());
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, value);
	}

	public void validate(ValidationContext ctx, SourceLine sourceLine) {
		if (value != null) {
			try {
				key.component.validate(ctx, value);
			} catch (Throwable cause) {
				throw new InvalidRecipeComponentException(this, cause).source(sourceLine);
			}
		} else if (!key.optional()) {
			throw new InvalidRecipeComponentException(this, new MissingRequiredValueException()).source(sourceLine);
		}
	}
}
