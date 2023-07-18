package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.platform.IngredientPlatformHelper;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class OutputItem implements OutputReplacement {
	public static final OutputItem EMPTY = new OutputItem(ItemStack.EMPTY, Double.NaN, 0, 0);

	public static OutputItem of(ItemStack item, double chance, int minRolls, int maxRolls) {
		return item.isEmpty() ? EMPTY : new OutputItem(item, chance, minRolls, maxRolls);
	}
	public static OutputItem of(ItemStack item, double chance) {
		return OutputItem.of(item, chance, 0, 0);
	}

	public static OutputItem of(Object o) {
		if (o instanceof OutputItem out) {
			return out;
		} else if (o instanceof ItemStack stack) {
			return of(stack, Double.NaN, 0, 0);
		}

		return of(ItemStackJS.of(o), Double.NaN, 0, 0);
	}

	public final ItemStack item;
	public final double chance;
	public final int minRolls;
	public final int maxRolls;

	protected OutputItem(ItemStack item, double chance, int minRolls, int maxRolls) {
		this.item = item;
		this.chance = chance;
		this.minRolls = minRolls;
		this.maxRolls = maxRolls;
	}

	protected OutputItem(ItemStack item, double chance) {
		this.item = item;
		this.chance = chance;
		this.minRolls = 0;
		this.maxRolls = 0;
	}

	public OutputItem withCount(int count) {
		return new OutputItem(item.kjs$withCount(count), chance, minRolls, maxRolls);
	}

	public OutputItem withChance(double chance) {
		return new OutputItem(item, chance, minRolls, maxRolls);
	}

	public OutputItem minRolls(int minRolls) {
		return new OutputItem(item, chance, minRolls, maxRolls);
	}

	public OutputItem maxRolls(int maxRolls) {
		return new OutputItem(item, chance, minRolls, maxRolls);
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
			var replacement = new OutputItem(item.copy(), o.chance, o.minRolls, o.maxRolls);
			replacement.item.setCount(o.getCount());
			return replacement;
		}

		return new OutputItem(item.copy(), Double.NaN, 0, 0);
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
