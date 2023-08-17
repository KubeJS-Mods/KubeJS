package dev.latvian.mods.kubejs.core.mixin.forge;

import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.PartialNBTIngredient;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({PartialNBTIngredient.class, StrictNBTIngredient.class})
public abstract class TrivialIngredientsMixin extends AbstractIngredient {
    @Override
    public boolean kjs$canBeUsedForMatching() {
        return true;
    }
}
