package dev.latvian.mods.kubejs.core.mixin.fabric;

import dev.latvian.mods.kubejs.fabric.CustomIngredientKJS;
import net.fabricmc.fabric.impl.recipe.ingredient.builtin.NbtIngredient;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NbtIngredient.class)
public abstract class NbtIngredientMixin implements CustomIngredientKJS {
    @Shadow
    @Final
    private Ingredient base;

    @Override
    public boolean kjs$canBeUsedForMatching() {
        return base.kjs$canBeUsedForMatching();
    }
}
