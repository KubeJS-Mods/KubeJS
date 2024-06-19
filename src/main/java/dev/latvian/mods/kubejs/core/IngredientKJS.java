package dev.latvian.mods.kubejs.core;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.bindings.SizedIngredientWrapper;
import dev.latvian.mods.kubejs.ingredient.IngredientHelper;
import dev.latvian.mods.kubejs.item.ChancedIngredient;
import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.util.WithCodec;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

@RemapPrefixForJS("kjs$")
public interface IngredientKJS extends IngredientSupplierKJS, ItemPredicate, InputReplacement, WithCodec {
	default Ingredient kjs$self() {
		throw new NoMixinException();
	}

	@Override
	default ItemStack[] kjs$getStackArray() {
		return kjs$self().getItems();
	}

	default Ingredient kjs$and(Ingredient ingredient) {
		return ingredient == Ingredient.EMPTY ? kjs$self() : this == Ingredient.EMPTY ? ingredient : IngredientHelper.get().and(new Ingredient[]{kjs$self(), ingredient});
	}

	default Ingredient kjs$or(Ingredient ingredient) {
		return ingredient == Ingredient.EMPTY ? kjs$self() : this == Ingredient.EMPTY ? ingredient : IngredientHelper.get().or(new Ingredient[]{kjs$self(), ingredient});
	}

	default Ingredient kjs$subtract(Ingredient subtracted) {
		return IngredientHelper.get().subtract(kjs$self(), subtracted);
	}

	default SizedIngredient kjs$asStack() {
		if (kjs$self().isEmpty()) {
			return SizedIngredientWrapper.empty;
		}

		return new SizedIngredient(kjs$self(), 1);
	}

	default SizedIngredient kjs$withCount(int count) {
		return new SizedIngredient(kjs$self(), count);
	}

	default ChancedIngredient kjs$withChance(FloatProvider chance) {
		return new ChancedIngredient(kjs$self(), 1, chance);
	}

	@Override
	default boolean kjs$isWildcard() {
		return IngredientHelper.get().isWildcard(kjs$self());
	}

	@Override
	default Ingredient kjs$asIngredient() {
		return kjs$self();
	}

	@Override
	default Codec<?> getCodec(Context cx) {
		return Ingredient.CODEC;
	}

	@Override
	default Object replaceInput(Context cx, KubeRecipe recipe, ReplacementMatch match, InputReplacement original) {
		if (original instanceof SizedIngredientKJS s) {
			return new SizedIngredient(kjs$self(), s.kjs$self().count());
		}

		return this;
	}
}
