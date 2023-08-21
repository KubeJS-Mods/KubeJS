package dev.latvian.mods.kubejs.core.mixin.fabric;

import dev.latvian.mods.kubejs.core.IngredientKJS;
import dev.latvian.mods.kubejs.fabric.CustomIngredientKJS;
import net.fabricmc.fabric.api.recipe.v1.ingredient.FabricIngredient;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Ingredient.class)
public abstract class IngredientMatchingMixin implements IngredientKJS, FabricIngredient {
	@Override
	public boolean kjs$canBeUsedForMatching() {
		if (IngredientKJS.super.kjs$canBeUsedForMatching()) {
			return true;
		} else {
			var custom = getCustomIngredient();
			return custom != null && ((CustomIngredientKJS) custom).kjs$canBeUsedForMatching();
		}
	}
}
