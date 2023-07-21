package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValue;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.regex.Pattern;

public class ErroredRecipeJS extends RecipeJS {
	private final String context;

	public final BaseFunction dummyFunction = new BaseFunction() {
		@Override
		public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
			ConsoleJS.SERVER.warn("Tried to call a function on an errored recipe! (%s)".formatted(context));
			return this;
		}
	};

	public ErroredRecipeJS(RecipesEventJS event, RecipeExceptionJS rex) {
		this(event, rex.getMessage(), rex, null);
	}

	public ErroredRecipeJS(RecipesEventJS event, String errorMessage, RecipeExceptionJS rex, @Nullable Pattern skipError) {
		this.context = errorMessage;
		ConsoleJS.SERVER.error(errorMessage, rex, skipError);
		event.failedCount.incrementAndGet();
	}

	@Override
	public void deserialize(boolean merge) {
	}

	@Override
	public void serialize() {
	}

	@Override
	public boolean hasInput(ReplacementMatch match) {
		return false;
	}

	@Override
	public boolean replaceInput(ReplacementMatch match, InputReplacement with) {
		return false;
	}

	@Override
	public boolean hasOutput(ReplacementMatch match) {
		return false;
	}

	@Override
	public boolean replaceOutput(ReplacementMatch match, OutputReplacement with) {
		return false;
	}

	@Override
	public Map<String, RecipeComponentValue<?>> getAllValueMap() {
		return Map.of();
	}

	@Override
	public String toString() {
		return "Errored Recipe (" + context + ')';
	}
}
