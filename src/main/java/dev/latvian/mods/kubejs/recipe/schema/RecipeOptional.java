package dev.latvian.mods.kubejs.recipe.schema;

@FunctionalInterface
public interface RecipeOptional<T> {
	RecipeOptional<?> DEFAULT = type -> null;

	record Constant<T>(T value) implements RecipeOptional<T> {
		@Override
		public T getDefaultValue(RecipeSchemaType type) {
			return value;
		}
	}

	T getDefaultValue(RecipeSchemaType type);

	default boolean isDefault() {
		return this == DEFAULT;
	}
}
