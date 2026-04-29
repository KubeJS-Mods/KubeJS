package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.recipe.RecipeSchemaProvider;
import dev.latvian.mods.kubejs.util.Cast;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface RecipeOptional<T> {
	RecipeOptional<?> DEFAULT = new Unit<>() {
		@Override
		public @Nullable Object value() {
			return null;
		}

		@Override
		public String toString() {
			return "null";
		}
	};

	interface Unit<T> extends RecipeOptional<T> {
		@Nullable T value();

		@Override
		default T getDefaultValue(RecipeSchemaType type) {
			return value();
		}

		@Override
		@Nullable
		default T getInformativeValue() {
			return value();
		}

		@ApiStatus.Internal
		record Impl<T>(T value) implements Unit<T> {
		}
	}

	T getDefaultValue(RecipeSchemaType type);

	/// Gets a value that is used during [`data generation`][RecipeSchemaProvider] of recipe schema JSONs,
	/// as well as during debugging of recipe constructors.
	///
	/// This **needs to be** implemented if you intend to use data generation with a custom optional type
	@Nullable
	default T getInformativeValue() {
		return null;
	}

	default boolean isDefault() {
		return this == DEFAULT;
	}

	static <T> RecipeOptional<T> unit(@Nullable T value) {
		return value == null ? Cast.to(DEFAULT) : new Unit.Impl<>(value);
	}

	static <T> RecipeOptional<T> none() {
		return Cast.to(DEFAULT);
	}
}
