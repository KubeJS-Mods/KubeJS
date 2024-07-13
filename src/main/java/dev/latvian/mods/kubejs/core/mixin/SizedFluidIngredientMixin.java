package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.SizedFluidIngredientKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = SizedFluidIngredient.class, remap = false)
@RemapPrefixForJS("kjs$")
public abstract class SizedFluidIngredientMixin implements SizedFluidIngredientKJS {
}
