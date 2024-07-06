package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValue;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.util.HideFromJS;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public class ErroredKubeRecipe extends KubeRecipe {
	private final String context;

	public final BaseFunction dummyFunction = new BaseFunction() {
		@Override
		public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
			ConsoleJS.SERVER.warn("Tried to call a function on an errored recipe! (%s)".formatted(context));
			return this;
		}
	};

	public ErroredKubeRecipe(RecipesKubeEvent event, KubeRuntimeException rex) {
		this(event, rex.getMessage(), rex, null);
	}

	public ErroredKubeRecipe(RecipesKubeEvent event, String errorMessage, KubeRuntimeException rex, @Nullable Pattern skipError) {
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
	public boolean hasInput(Context cx, ReplacementMatchInfo match) {
		return false;
	}

	@Override
	public boolean replaceInput(Context cx, ReplacementMatchInfo match, Object with) {
		return false;
	}

	@Override
	public boolean hasOutput(Context cx, ReplacementMatchInfo match) {
		return false;
	}

	@Override
	public boolean replaceOutput(Context cx, ReplacementMatchInfo match, Object with) {
		return false;
	}

	@Override
	@HideFromJS
	public RecipeComponentValue<?>[] getRecipeComponentValues() {
		return RecipeComponentValue.EMPTY_ARRAY;
	}

	@Override
	public String toString() {
		return "Errored Recipe (" + context + ')';
	}
}
