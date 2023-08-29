package dev.latvian.mods.kubejs.core.mixin.fabric;

import dev.latvian.mods.kubejs.fabric.CustomIngredientKJS;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CustomIngredient.class)
public interface CustomIngredientMixin extends CustomIngredientKJS {
}
