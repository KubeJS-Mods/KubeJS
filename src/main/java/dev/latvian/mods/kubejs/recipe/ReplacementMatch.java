package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.rhino.Context;

public interface ReplacementMatch {
	ReplacementMatch NONE = new ReplacementMatch() {
		@Override
		public String toString() {
			return "NONE";
		}
	};

	static ReplacementMatch of(Context cx, Object o) {
		if (o == null) {
			return NONE;
		} else if (o instanceof ReplacementMatch m) {
			return m;
		}

		var in = IngredientJS.wrap(cx, o);

		if (in.isEmpty()) {
			return NONE;
		} else if (in.getItems().length == 1) {
			return new SingleItemMatch(in.getItems()[0]);
		}

		// FIXME: Add exact: true/false support
		// TODO: Add support for other types of replacements
		return new IngredientMatch(in, false);
	}
}
