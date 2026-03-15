package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.IngredientKJS;
import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.stream.Stream;

@Mixin(Ingredient.class)
@RemapPrefixForJS("kjs$")
public abstract class IngredientMixin implements IngredientKJS {
	@Mutable
	@Shadow
	public HolderSet<Item> values;

	@Override
	public Ingredient kjs$self() {
		return (Ingredient) (Object) this;
	}

	@Shadow
	@HideFromJS
	public abstract Stream<Holder<Item>> items();

	@Shadow
	public abstract ICustomIngredient getCustomIngredient();

	@Shadow
	public abstract boolean isCustom();

	@Override
	public boolean kjs$canBeUsedForMatching() {
		return !isCustom() || ((ItemPredicate) getCustomIngredient()).kjs$canBeUsedForMatching();
	}
}
