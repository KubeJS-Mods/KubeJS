package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.core.IngredientKJS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Ingredient.class)
public abstract class IngredientMixin implements IngredientKJS {
	@Shadow
	private ItemStack[] itemStacks;

	@Shadow
	protected abstract void dissolve();

	@Override
	public ItemStack[] getItemsKJS() {
		dissolve();
		return itemStacks;
	}
}
