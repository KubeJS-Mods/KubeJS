package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.item.InputItem;

public interface InputReplacement {
	static InputReplacement of(Object o) {
		return o instanceof InputReplacement r ? r : InputItem.of(o);
	}

	default InputReplacementTransformer.Replacement transform(InputReplacementTransformer transformer) {
		return new InputReplacementTransformer.Replacement(this, transformer);
	}

	default Object replaceInput(KubeRecipe recipe, ReplacementMatch match, InputReplacement original) {
		return this;
	}
}
