package dev.latvian.mods.kubejs.recipe.ingredient;

import dev.latvian.mods.kubejs.core.IngredientKJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ItemStackSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class KubeJSIngredient extends Ingredient implements IngredientKJS {
	private static final Ingredient.Value[] EMPTY_VALUES = new Ingredient.Value[0];

	public KubeJSIngredient(Supplier<? extends IngredientType<? extends KubeJSIngredient>> type) {
		super(Stream.empty(), type);
		values = EMPTY_VALUES;
	}

	@Override
	public ItemStack[] getItems() {
		if (this.itemStacks == null) {
			dissolve();
		}

		return this.itemStacks;
	}

	protected void dissolve() {
		if (this.itemStacks == null) {
			ItemStackSet stacks = new ItemStackSet();

			for (var stack : ItemStackJS.getList()) {
				if (test(stack)) {
					stacks.add(stack);
				}
			}

			this.itemStacks = stacks.toArray();
		}
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public boolean kjs$canBeUsedForMatching() {
		// all of our ingredients should be safe for matching,
		// unless somebody does something *really* weird from scripts
		return true;
	}
}
