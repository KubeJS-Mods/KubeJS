package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.SizedIngredientKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SizedIngredient.class)
@RemapPrefixForJS("kjs$")
public abstract class SizedIngredientMixin implements SizedIngredientKJS {
}
