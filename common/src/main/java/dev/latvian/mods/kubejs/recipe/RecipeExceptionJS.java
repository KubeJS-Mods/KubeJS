package dev.latvian.mods.kubejs.recipe;

public class RecipeExceptionJS extends IllegalArgumentException {
	public boolean error;

	public RecipeExceptionJS(String m) {
		super(m);
		error = false;
	}

	public RecipeExceptionJS(String m, Throwable cause) {
		super(m, cause);
		error = false;
	}

	@Override
	public String toString() {
		return getLocalizedMessage();
	}

	public RecipeExceptionJS error() {
		error = true;
		return this;
	}
}