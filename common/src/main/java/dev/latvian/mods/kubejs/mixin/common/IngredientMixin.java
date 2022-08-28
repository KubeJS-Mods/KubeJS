package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.core.IngredientKJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Ingredient.class)
@RemapPrefixForJS("kjs$")
public abstract class IngredientMixin implements IngredientKJS {
	@Override
	public Ingredient kjs$self() {
		return (Ingredient) (Object) this;
	}

	@Shadow
	@HideFromJS
	public abstract ItemStack[] getItems();
}
