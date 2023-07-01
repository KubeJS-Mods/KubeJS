package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.platform.IngredientPlatformHelper;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class OutputItem implements OutputReplacement {
	public static final OutputItem EMPTY = new OutputItem(ItemStack.EMPTY, Double.NaN);

	public static OutputItem of(ItemStack item, double chance) {
		return item.isEmpty() ? EMPTY : new OutputItem(item, chance);
	}

	public static OutputItem of(Object o) {
		if (o instanceof OutputItem out) {
			return out;
		} else if (o instanceof ItemStack stack) {
			return of(stack, Double.NaN);
		}

		return of(ItemStackJS.of(o), Double.NaN);
	}

	public final ItemStack item;
	public final double chance;

	protected OutputItem(ItemStack item, double chance) {
		this.item = item;
		this.chance = chance;
	}

	public OutputItem withCount(int count) {
		return new OutputItem(item.kjs$withCount(count), chance);
	}

	public OutputItem withChance(double chance) {
		return new OutputItem(item, chance);
	}

	public boolean hasChance() {
		return !Double.isNaN(chance);
	}

	public double getChance() {
		return chance;
	}

	public int getCount() {
		return item.getCount();
	}

	public CompoundTag getNbt() {
		return item.getTag();
	}

	@Override
	public String toString() {
		return item.kjs$toItemString();
	}

	public boolean isEmpty() {
		return this == EMPTY;
	}

	@Override
	public Object replaceOutput(RecipeJS recipe, ReplacementMatch match, OutputReplacement original) {
		if (original instanceof OutputItem o) {
			var replacement = new OutputItem(item.copy(), o.chance);
			replacement.item.setCount(o.getCount());
			return replacement;
		}

		return new OutputItem(item.copy(), Double.NaN);
	}

	@Deprecated
	public InputItem ignoreNBT() {
		var console = ConsoleJS.getCurrent(ConsoleJS.SERVER);
		console.warn("You don't need to call .ignoreNBT() anymore, all item ingredients ignore NBT by default!");
		return InputItem.of(item.getItem().kjs$asIngredient(), item.getCount());
	}

	public InputItem weakNBT() {
		return InputItem.of(IngredientPlatformHelper.get().weakNBT(item), item.getCount());
	}

	public InputItem strongNBT() {
		return InputItem.of(IngredientPlatformHelper.get().strongNBT(item), item.getCount());
	}
}
