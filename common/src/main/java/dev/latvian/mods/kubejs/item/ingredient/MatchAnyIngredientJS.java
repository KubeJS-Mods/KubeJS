package dev.latvian.mods.kubejs.item.ingredient;

import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.util.ListJS;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class MatchAnyIngredientJS implements IngredientJS, Consumer<IngredientJS> {
	public final List<IngredientJS> ingredients = new ArrayList<>();

	public MatchAnyIngredientJS add(@Nullable Object ingredient) {
		IngredientJS i = IngredientJS.of(ingredient);

		if (i != ItemStackJS.EMPTY) {
			ingredients.add(i);
		}

		return this;
	}

	public MatchAnyIngredientJS addAll(Object ingredients) {
		for (var o : ListJS.orSelf(ingredients)) {
			add(o);
		}

		return this;
	}

	@Override
	public boolean test(ItemStackJS stack) {
		if (stack.isEmpty()) {
			return false;
		}

		for (var ingredient : ingredients) {
			if (ingredient.test(stack)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean testVanilla(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}

		for (var ingredient : ingredients) {
			if (ingredient.testVanilla(stack)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean testVanillaItem(Item item) {
		if (item == Items.AIR) {
			return false;
		}

		for (var ingredient : ingredients) {
			if (ingredient.testVanillaItem(item)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Set<ItemStackJS> getStacks() {
		Set<ItemStackJS> set = new LinkedHashSet<>();

		for (var ingredient : ingredients) {
			set.addAll(ingredient.getStacks());
		}

		return set;
	}

	@Override
	public Set<Item> getVanillaItems() {
		Set<Item> set = new LinkedHashSet<>();

		for (var ingredient : ingredients) {
			set.addAll(ingredient.getVanillaItems());
		}

		return set;
	}

	@Override
	public boolean isEmpty() {
		for (var i : ingredients) {
			if (!i.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void accept(IngredientJS ingredient) {
		ingredients.add(ingredient);
	}

	@Override
	public IngredientJS copy() {
		MatchAnyIngredientJS i = new MatchAnyIngredientJS();

		for (var in : ingredients) {
			i.ingredients.add(in.copy());
		}

		return i;
	}

	@Override
	public String toString() {
		return ingredients.toString();
	}

	@Override
	public boolean isInvalidRecipeIngredient() {
		for (var i : ingredients) {
			if (i.isInvalidRecipeIngredient()) {
				return true;
			}
		}

		return false;
	}
}