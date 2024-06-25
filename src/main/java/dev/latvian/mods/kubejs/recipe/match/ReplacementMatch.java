package dev.latvian.mods.kubejs.recipe.match;

import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.rhino.Context;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

public interface ReplacementMatch {
	ReplacementMatch NONE = new ReplacementMatch() {
		@Override
		public String toString() {
			return "NONE";
		}
	};

	static ReplacementMatch wrap(Context cx, Object o) {
		if (o == null) {
			return NONE;
		} else if (o instanceof ReplacementMatch m) {
			return m;
		} else if (o instanceof FluidIngredient fi) {
			return new FluidIngredientMatch(fi);
		} else if (o instanceof FluidStack fs) {
			return new SingleFluidMatch(fs);
		}

		var in = IngredientJS.wrap(((KubeJSContext) cx).getRegistries(), o);

		if (in.isEmpty()) {
			return NONE;
		} else if (in.getItems().length == 1) {
			return new SingleItemMatch(in.getItems()[0]);
		}

		return new IngredientMatch(in);
	}
}
