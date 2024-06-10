package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.rhino.Context;

public interface OutputReplacement {
	static OutputReplacement of(Context cx, Object o) {
		return o instanceof OutputReplacement r ? r : ItemStackJS.of(o);
	}

	default OutputReplacementTransformer.Replacement transform(OutputReplacementTransformer transformer) {
		return new OutputReplacementTransformer.Replacement(this, transformer);
	}

	default Object replaceOutput(Context cx, KubeRecipe recipe, ReplacementMatch match, OutputReplacement original) {
		return this;
	}
}
