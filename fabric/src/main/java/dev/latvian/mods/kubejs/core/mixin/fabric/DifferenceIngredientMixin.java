package dev.latvian.mods.kubejs.core.mixin.fabric;

import dev.latvian.mods.kubejs.fabric.CustomIngredientKJS;
import net.fabricmc.fabric.impl.recipe.ingredient.builtin.DifferenceIngredient;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DifferenceIngredient.class)
public abstract class DifferenceIngredientMixin implements CustomIngredientKJS {
	@Shadow
	@Final
	private Ingredient base;

	@Shadow
	@Final
	private Ingredient subtracted;

	@Override
	public boolean kjs$canBeUsedForMatching() {
		return base.kjs$canBeUsedForMatching() && subtracted.kjs$canBeUsedForMatching();
	}
}
