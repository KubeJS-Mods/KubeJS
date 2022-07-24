package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.item.crafting.Ingredient;

@RemapPrefixForJS("kjs$")
public interface IngredientKJS extends AsKJS<IngredientJS> {
	@Override
	default IngredientJS asKJS() {
		return IngredientJS.of(this);
	}

	default Ingredient kjs$self() {
		throw new NoMixinException();
	}
}
