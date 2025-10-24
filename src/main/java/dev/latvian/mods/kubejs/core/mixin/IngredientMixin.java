package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.IngredientKJS;
import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
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

	@Shadow
	public abstract ICustomIngredient getCustomIngredient();

	@Shadow
	public abstract boolean isCustom();

	@Override
	public boolean kjs$canBeUsedForMatching() {
		return !isCustom() || ((ItemPredicate) getCustomIngredient()).kjs$canBeUsedForMatching();
	}
}
