package dev.latvian.mods.kubejs.recipe.schema;

@FunctionalInterface
public interface RecipeOptional<T> {
	RecipeOptional<?> DEFAULT = type -> null;

	T getDefaultValue(RecipeSchemaType type);

	record Constant<T>(T value) implements RecipeOptional<T> {
		@Override
		public T getDefaultValue(RecipeSchemaType type) {
			return value;
		}
	}

	default boolean isDefault() {
		return this == DEFAULT;
	}
}
