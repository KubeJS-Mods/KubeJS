package dev.latvian.mods.kubejs.bindings;

import dev.latvian.mods.kubejs.ingredient.TagIngredient;
import dev.latvian.mods.kubejs.ingredient.WildcardIngredient;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

@Info("Various Ingredient related helper methods")
public interface IngredientWrapper {
	@Info("A completely empty ingredient that will only match air")
	Ingredient none = Ingredient.EMPTY;
	@Info("An ingredient that matches everything")
	Ingredient all = WildcardIngredient.INSTANCE.toVanilla();

	@Info("Returns an ingredient of the input")
	static Ingredient of(Ingredient ingredient) {
		return ingredient;
	}

	@Info("Returns an ingredient of the input, with the specified count")
	static SizedIngredient of(Ingredient ingredient, int count) {
		return ingredient.kjs$withCount(count);
	}

	@Info("""
		Checks if the passed in object is an Ingredient.
		Note that this does not mean it will not function as an Ingredient if passed to something that requests one.
		""")
	static boolean isIngredient(@Nullable Object o) {
		return o instanceof Ingredient;
	}

	static ItemStack first(Ingredient ingredient) {
		return ingredient.kjs$getFirst();
	}

	@Nullable
	static TagKey<Item> tagKeyOf(Ingredient in) {
		if (!in.isCustom() && in.getValues().length == 1 && in.getValues()[0] instanceof Ingredient.TagValue value) {
			return value.tag();
		} else if (in.getCustomIngredient() instanceof TagIngredient tin) {
			return tin.tagKey;
		} else {
			return null;
		}
	}
}