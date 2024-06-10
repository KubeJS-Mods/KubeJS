package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.FluidStackKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FluidStack.class)
@RemapPrefixForJS("kjs$")
public abstract class FluidStackMixin implements FluidStackKJS {
}
