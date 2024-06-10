package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.bindings.SizedIngredientWrapper;
import dev.latvian.mods.rhino.Context;

public interface InputReplacement {
	static InputReplacement of(Context cx, Object o) {
		return o instanceof InputReplacement r ? r : (InputReplacement) (Object) SizedIngredientWrapper.wrap(cx, o);
	}

	default InputReplacementTransformer.Replacement transform(InputReplacementTransformer transformer) {
		return new InputReplacementTransformer.Replacement(this, transformer);
	}

	default Object replaceInput(Context cx, KubeRecipe recipe, ReplacementMatch match, InputReplacement original) {
		return this;
	}
}
