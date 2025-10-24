package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.CustomIngredientKJS;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.DifferenceIngredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DifferenceIngredient.class)
public abstract class DifferenceIngredientMixin implements CustomIngredientKJS {
	@Shadow(remap = false)
	@Final
	private Ingredient base;

	@Shadow(remap = false)
	@Final
	private Ingredient subtracted;

	@Override
	public boolean kjs$canBeUsedForMatching() {
		return base.kjs$canBeUsedForMatching() && subtracted.kjs$canBeUsedForMatching();
	}
}
