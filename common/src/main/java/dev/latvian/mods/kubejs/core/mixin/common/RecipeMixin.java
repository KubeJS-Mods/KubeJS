package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Recipe.class)
public interface RecipeMixin extends RecipeKJS {
	@Shadow
	@HideFromJS
	String getGroup();

	@Shadow
	@HideFromJS
	RecipeType<?> getType();
}
