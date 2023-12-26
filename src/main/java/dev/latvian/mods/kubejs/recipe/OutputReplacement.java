package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.item.OutputItem;

public interface OutputReplacement {
	static OutputReplacement of(Object o) {
		return o instanceof OutputReplacement r ? r : OutputItem.of(o);
	}

	default OutputReplacementTransformer.Replacement transform(OutputReplacementTransformer transformer) {
		return new OutputReplacementTransformer.Replacement(this, transformer);
	}

	default Object replaceOutput(RecipeJS recipe, ReplacementMatch match, OutputReplacement original) {
		return this;
	}
}
