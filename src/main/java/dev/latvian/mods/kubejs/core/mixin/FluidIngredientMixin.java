package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.FluidIngredientKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = FluidIngredient.class, remap = false)
@RemapPrefixForJS("kjs$")
public abstract class FluidIngredientMixin implements FluidIngredientKJS {
	@Override
	public FluidIngredient kjs$self() {
		return (FluidIngredient) (Object) this;
	}
}
