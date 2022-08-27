package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.item.ItemStackSet;
import dev.latvian.mods.kubejs.item.ingredient.AndIngredient;
import dev.latvian.mods.kubejs.item.ingredient.IngredientStack;
import dev.latvian.mods.kubejs.item.ingredient.NotIngredient;
import dev.latvian.mods.kubejs.item.ingredient.OrIngredient;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.LinkedHashSet;
import java.util.Set;

@RemapPrefixForJS("kjs$")
public interface IngredientKJS {
	default Ingredient kjs$self() {
		throw new NoMixinException();
	}

	default boolean kjs$testItem(Item item) {
		return kjs$self().test(item.getDefaultInstance());
	}

	default void kjs$gatherStacks(ItemStackSet set) {
		for (ItemStack stack : kjs$self().getItems()) {
			set.add(stack);
		}
	}

	default ItemStackSet kjs$getStacks() {
		ItemStackSet set = new ItemStackSet();
		kjs$gatherStacks(set);
		return set;
	}

	default void kjs$gatherItemTypes(Set<Item> set) {
		for (var item : KubeJSRegistries.items()) {
			if (item != Items.AIR && kjs$testItem(item)) {
				set.add(item);
			}
		}
	}

	default Set<Item> kjs$getItemTypes() {
		Set<Item> set = new LinkedHashSet<>();
		kjs$gatherItemTypes(set);
		return set;
	}

	default Set<String> kjs$getItemIds() {
		Set<String> ids = new LinkedHashSet<>();

		for (var item : kjs$getItemTypes()) {
			var id = KubeJSRegistries.items().getId(item);

			if (id != null) {
				ids.add(id.toString());
			}
		}

		return ids;
	}

	default ItemStack kjs$getFirst() {
		return kjs$getStacks().getFirst();
	}

	default Ingredient kjs$and(Ingredient ingredient) {
		return ingredient == Ingredient.EMPTY ? kjs$self() : this == Ingredient.EMPTY ? ingredient : new AndIngredient(new Ingredient[]{kjs$self(), ingredient});
	}

	default Ingredient kjs$or(Ingredient ingredient) {
		return ingredient == Ingredient.EMPTY ? kjs$self() : this == Ingredient.EMPTY ? ingredient : new OrIngredient(new Ingredient[]{kjs$self(), ingredient});
	}

	default Ingredient kjs$not() {
		return new NotIngredient(kjs$self());
	}

	default IngredientStack kjs$asStack() {
		return new IngredientStack(kjs$self(), 1);
	}

	default Ingredient kjs$withCount(int count) {
		return new IngredientStack(kjs$self(), count);
	}

	default boolean kjs$isInvalidRecipeIngredient() {
		return false;
	}
}
