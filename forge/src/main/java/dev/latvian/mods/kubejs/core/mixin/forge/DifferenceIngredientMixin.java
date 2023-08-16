package dev.latvian.mods.kubejs.core.mixin.forge;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.DifferenceIngredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DifferenceIngredient.class)
public abstract class DifferenceIngredientMixin extends AbstractIngredient {
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
