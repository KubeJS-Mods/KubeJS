package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.RecipeInputKJS;
import net.minecraft.world.item.crafting.RecipeInput;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RecipeInput.class)
public interface RecipeInputMixin extends RecipeInputKJS {
}
