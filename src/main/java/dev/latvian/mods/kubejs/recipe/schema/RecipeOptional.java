package dev.latvian.mods.kubejs.recipe.schema;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface RecipeOptional<T> {
	RecipeOptional<?> DEFAULT = new Constant<>(null);

	record Constant<T>(T value) implements RecipeOptional<T> {
		@Override
		public T getDefaultValue(RecipeSchemaType type) {
			return value;
		}

		@Override
		@Nullable
		public T getInformativeValue() {
			return value;
		}

		@Override
		public T getValueForDataGeneration() {
			return value;
		}

		@Override
		@NotNull
		public String toString() {
			return String.valueOf(value);
		}
	}

	T getDefaultValue(RecipeSchemaType type);

	@Nullable
	default T getInformativeValue() {
		return null;
	}

	/**
	 * Gets a value that can be encoded into json by a recipe key's codec.
	 * For use in {@link dev.latvian.mods.kubejs.recipe.RecipeSchemaProvider data generation}
	 * <p>
	 * This <strong>must</strong> be implemented if you intend to use data generation with a custom optional type
	 */
	default T getValueForDataGeneration() {
		return null;
	}

	default boolean isDefault() {
		return this == DEFAULT;
	}
}
