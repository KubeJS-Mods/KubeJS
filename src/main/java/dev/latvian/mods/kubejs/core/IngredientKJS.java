package dev.latvian.mods.kubejs.core;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.bindings.SizedIngredientWrapper;
import dev.latvian.mods.kubejs.ingredient.WildcardIngredient;
import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.recipe.match.Replaceable;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.kubejs.util.WithCodec;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DifferenceIngredient;
import net.neoforged.neoforge.common.crafting.IntersectionIngredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

@RemapPrefixForJS("kjs$")
public interface IngredientKJS extends ItemPredicate, Replaceable, WithCodec {
	default Ingredient kjs$self() {
		throw new NoMixinException();
	}

	@Override
	default ItemStack[] kjs$getStackArray() {
		return kjs$self().getItems();
	}

	default Ingredient kjs$and(Ingredient ingredient) {
		return ingredient == Ingredient.EMPTY ? kjs$self() : this == Ingredient.EMPTY ? ingredient : IntersectionIngredient.of(kjs$self(), ingredient);
	}

	default Ingredient kjs$or(Ingredient ingredient) {
		return ingredient == Ingredient.EMPTY ? kjs$self() : this == Ingredient.EMPTY ? ingredient : CompoundIngredient.of(kjs$self(), ingredient);
	}

	default Ingredient kjs$except(Ingredient subtracted) {
		return DifferenceIngredient.of(kjs$self(), subtracted);
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

	@Override
	default boolean kjs$isWildcard() {
		return kjs$self().getCustomIngredient() == WildcardIngredient.INSTANCE;
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
	default Object replaceThisWith(Context cx, Object with) {
		var t = kjs$self();
		var r = IngredientJS.wrap(RegistryAccessContainer.of(cx), with);

		if (!r.equals(t)) {
			return r;
		}

		return this;
	}
}
