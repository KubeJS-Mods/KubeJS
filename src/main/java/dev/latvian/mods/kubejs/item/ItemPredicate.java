package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.core.IngredientSupplierKJS;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.IngredientWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

@RemapPrefixForJS("kjs$")
public interface ItemPredicate extends Predicate<ItemStack>, IngredientSupplierKJS {
	TypeInfo TYPE_INFO = TypeInfo.of(ItemPredicate.class);
	ItemPredicate NONE = stack -> false;
	ItemPredicate ALL = stack -> true;

	@Override
	boolean test(ItemStack itemStack);

	private static ItemPredicate simplify(Ingredient in) {
		return in.isEmpty() ? NONE : in.kjs$isWildcard() ? ALL : in;
	}

	static ItemPredicate wrap(Context cx, Object from) {
		return switch (from) {
			case null -> NONE;
			case BaseFunction func -> (ItemPredicate) cx.createInterfaceAdapter(TYPE_INFO, func);
			case String s -> switch (s) {
				case "*" -> ALL;
				case "", "-" -> NONE;
				case String s1 when s1.isBlank() -> NONE;
				default -> simplify(IngredientWrapper.wrap(cx, from));
			};
			default -> simplify(IngredientWrapper.wrap(cx, from));
		};
	}

	default boolean kjs$testItem(Item item) {
		return test(item.getDefaultInstance());
	}

	default ItemStack[] kjs$getStackArray() {
		return ItemWrapper.getList().stream().filter(this).toArray(ItemStack[]::new);
	}

	default ItemStackSet kjs$getStacks() {
		return new ItemStackSet(kjs$getStackArray());
	}

	default ItemStackSet kjs$getDisplayStacks() {
		var set = new ItemStackSet();

		for (var stack : ItemWrapper.getList()) {
			if (test(stack)) {
				set.add(stack);
			}
		}

		return set;
	}

	default boolean kjs$isWildcard() {
		return this == ALL;
	}

	default Set<Item> kjs$getItemTypes() {
		var items = kjs$getStackArray();

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
		var items = kjs$getStackArray();

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
		for (var stack : kjs$getStackArray()) {
			if (!stack.isEmpty()) {
				return stack;
			}
		}

		return ItemStack.EMPTY;
	}

	/**
	 * Marks whether an ingredient is safe to be used to match recipe filters during the recipe event.
	 * (The answer is usually no for non-Vanilla ingredients, but can be overridden manually by addons or downstream mods with integration.)
	 */
	default boolean kjs$canBeUsedForMatching() {
		return true;
	}

	@Override
	default Ingredient kjs$asIngredient() {
		return Ingredient.of(kjs$getStackArray());
	}
}
