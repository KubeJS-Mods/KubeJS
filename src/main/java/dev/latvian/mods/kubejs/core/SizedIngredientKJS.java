package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.item.ChancedIngredient;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.recipe.match.Replaceable;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.rhino.Context;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

public interface SizedIngredientKJS extends Replaceable, IngredientSupplierKJS {
	default SizedIngredient kjs$self() {
		return (SizedIngredient) (Object) this;
	}

	@Override
	default Object replaceThisWith(Context cx, Object with) {
		var ingredient = IngredientJS.wrap(((KubeJSContext) cx).getRegistries(), with);

		if (!ingredient.equals(kjs$self().ingredient())) {
			return new SizedIngredient(ingredient, kjs$self().count());
		}

		return this;
	}

	default ChancedIngredient kjs$withChance(FloatProvider chance) {
		return new ChancedIngredient(kjs$self().ingredient(), kjs$self().count(), chance);
	}

	@Override
	default Ingredient kjs$asIngredient() {
		return kjs$self().ingredient();
	}
}
