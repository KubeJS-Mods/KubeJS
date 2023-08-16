package dev.latvian.mods.kubejs.core.mixin.fabric;

import dev.latvian.mods.kubejs.fabric.CustomIngredientKJS;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net/fabricmc/fabric/impl/recipe/ingredient/builtin/CombinedIngredient")
public abstract class CombinedIngredientMixin implements CustomIngredientKJS {
    @Shadow
    @Final
    protected Ingredient[] ingredients;

    @Override
    public boolean kjs$canBeUsedForMatching() {
        for (Ingredient ingredient : ingredients) {
            if (!ingredient.kjs$canBeUsedForMatching()) {
                return false;
            }
        }

        return true;
    }
}
