package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.recipe.OutputItemTransformer;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
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

	public OutputItem(ItemStack item, double chance) {
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

	public OutputItem copyWithProperties(OutputItem original) {
		return isEmpty() ? this : new OutputItem(item.kjs$withCount(original.item.getCount()), original.chance);
	}

	@Override
	public <T> T replaceOutput(RecipeKJS recipe, ReplacementMatch match, T original) {
		return (T) copyWithProperties((OutputItem) original);
	}

	public OutputItemTransformer.Replacement transform(OutputItemTransformer transformer) {
		return new OutputItemTransformer.Replacement(this, transformer);
	}
}
