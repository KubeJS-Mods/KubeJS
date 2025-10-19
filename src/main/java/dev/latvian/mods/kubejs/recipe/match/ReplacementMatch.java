package dev.latvian.mods.kubejs.recipe.match;

import dev.latvian.mods.kubejs.plugin.builtin.wrapper.IngredientWrapper;
import dev.latvian.mods.rhino.Context;

public interface ReplacementMatch {
	ReplacementMatch NONE = new ReplacementMatch() {
		@Override
		public String toString() {
			return "NONE";
		}
	};

	static ReplacementMatch wrap(Context cx, Object o) {
		return switch (o) {
			case null -> NONE;
			case ReplacementMatch m -> m;
			default -> {
				var in = IngredientWrapper.wrap(cx, o);
				yield in.isEmpty() ? NONE : in;
			}
		};
	}
}
