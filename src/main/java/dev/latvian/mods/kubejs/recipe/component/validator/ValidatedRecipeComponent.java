package dev.latvian.mods.kubejs.recipe.component.validator;

import dev.latvian.mods.kubejs.error.EmptyRecipeComponentValueException;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentWithParent;

public record ValidatedRecipeComponent<T>(RecipeComponent<T> parent, RecipeComponentValidator validator) implements RecipeComponentWithParent<T> {
	@Override
	public RecipeComponent<T> parentComponent() {
		return parent;
	}

	@Override
	public void validate(T value) {
		if (!validator.isValid(this, value)) {
			throw new EmptyRecipeComponentValueException(this);
		}
	}
}
