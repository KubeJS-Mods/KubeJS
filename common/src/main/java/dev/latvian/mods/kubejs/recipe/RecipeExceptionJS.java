package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.util.MutedError;

public class RecipeExceptionJS extends IllegalArgumentException implements MutedError {
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
		var sb = new StringBuilder();
		sb.append(getMessage());
		if (error) {
			sb.append(" [error]");
		}

		// append cause as well since RecipeExceptions can swallow underlying problems
		if (getCause() != null) {
			sb.append("\ncause: ");
			sb.append(getCause());
		}

		return sb.toString();
	}

	public RecipeExceptionJS error() {
		error = true;
		return this;
	}
}