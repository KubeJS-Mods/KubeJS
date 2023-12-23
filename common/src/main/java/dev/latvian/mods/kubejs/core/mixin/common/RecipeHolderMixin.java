package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.core.RecipeHolderKJS;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RecipeHolder.class)
public interface RecipeHolderMixin extends RecipeHolderKJS {

}
