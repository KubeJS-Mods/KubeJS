package dev.latvian.mods.kubejs.core;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ItemStackSet;
import dev.latvian.mods.kubejs.item.ingredient.IngredientStack;
import dev.latvian.mods.kubejs.platform.IngredientPlatformHelper;
import dev.latvian.mods.rhino.mod.util.JsonSerializable;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RemapPrefixForJS("kjs$")
public interface IngredientKJS extends IngredientSupplierKJS, JsonSerializable {
	default Ingredient kjs$self() {
		throw new NoMixinException();
	}

	default boolean kjs$testItem(Item item) {
		return kjs$self().test(item.getDefaultInstance());
	}

	default ItemStackSet kjs$getStacks() {
		return new ItemStackSet(kjs$self().getItems());
	}

	default ItemStackSet kjs$getDisplayStacks() {
		var set = new ItemStackSet();

		for (var stack : ItemStackJS.getList()) {
			if (kjs$self().test(stack)) {
				set.add(stack);
			}
		}

		return set;
	}

	default Set<Item> kjs$getItemTypes() {
		var items = kjs$self().getItems();

		if (items.length == 1 && !items[0].isEmpty()) {
			return Set.of(items[0].getItem());
		}

		var set = new LinkedHashSet<Item>(items.length);

		for (var stack : items) {
			if (!stack.isEmpty()) {
				set.add(stack.getItem());
			}
		}

		return set;
	}

	default Set<String> kjs$getItemIds() {
		var items = kjs$self().getItems();

		if (items.length == 1 && !items[0].isEmpty()) {
			return Set.of(items[0].kjs$getId());
		}

		var ids = new LinkedHashSet<String>(items.length);

		for (var item : items) {
			if (!item.isEmpty()) {
				ids.add(item.kjs$getId());
			}
		}

		return ids;
	}

	default ItemStack kjs$getFirst() {
		for (var stack : kjs$self().getItems()) {
			if (!stack.isEmpty()) {
				return stack;
			}
		}

		return ItemStack.EMPTY;
	}

	default Ingredient kjs$and(Ingredient ingredient) {
		return ingredient == Ingredient.EMPTY ? kjs$self() : this == Ingredient.EMPTY ? ingredient : IngredientPlatformHelper.get().and(new Ingredient[]{kjs$self(), ingredient});
	}

	default Ingredient kjs$or(Ingredient ingredient) {
		return ingredient == Ingredient.EMPTY ? kjs$self() : this == Ingredient.EMPTY ? ingredient : IngredientPlatformHelper.get().or(new Ingredient[]{kjs$self(), ingredient});
	}

	default Ingredient kjs$subtract(Ingredient subtracted) {
		return IngredientPlatformHelper.get().subtract(kjs$self(), subtracted);
	}

	default IngredientStack kjs$asStack() {
		return (IngredientStack) IngredientPlatformHelper.get().stack(kjs$self(), 1);
	}

	default Ingredient kjs$withCount(int count) {
		return count < 1 ? Ingredient.EMPTY : count == 1 ? kjs$self() : IngredientPlatformHelper.get().stack(kjs$self(), count);
	}

	default boolean kjs$isInvalidRecipeIngredient() {
		return this == Ingredient.EMPTY;
	}

	default List<Ingredient> kjs$unwrapStackIngredient() {
		return List.of(kjs$self());
	}

	default boolean kjs$isWildcard() {
		return false;
	}

	@Override
	default Ingredient kjs$asIngredient() {
		return kjs$self();
	}

	@Override
	default JsonElement toJsonJS() {
		return kjs$self().toJson();
	}
}
