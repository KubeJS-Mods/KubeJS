package dev.latvian.mods.kubejs.item;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.Context;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class OutputItem implements OutputReplacement {
	public static final OutputItem EMPTY = new OutputItem(ItemStack.EMPTY, Double.NaN, null);

	public static OutputItem of(ItemStack item, double chance) {
		return item.isEmpty() ? EMPTY : new OutputItem(item, chance, null);
	}

	public static OutputItem of(Object from) {
		if (from instanceof OutputItem out) {
			return out;
		} else if (from instanceof ItemStack stack) {
			return of(stack, Double.NaN);
		}

		var item = ItemStackJS.of(from);

		if (item.isEmpty()) {
			return EMPTY;
		}

		var chance = Double.NaN;
		IntProvider rolls = null;

		if (from instanceof JsonObject j) {
			if (j.has("chance")) {
				chance = j.get("chance").getAsDouble();
			}

			if (j.has("minRolls") && j.has("maxRolls")) {
				rolls = UniformInt.of(j.get("minRolls").getAsInt(), j.get("maxRolls").getAsInt());
			}
		}

		return new OutputItem(item, chance, rolls);
	}

	public final ItemStack item;
	public final double chance; // Use FloatProvider in future?
	public final IntProvider rolls;

	@Deprecated
	protected OutputItem(ItemStack item, double chance) {
		this(item, chance, null);
	}

	protected OutputItem(ItemStack item, double chance, @Nullable IntProvider rolls) {
		this.item = item;
		this.chance = chance;
		this.rolls = rolls;
	}

	public OutputItem withCount(int count) {
		return new OutputItem(item.kjs$withCount(count), chance, rolls);
	}

	public OutputItem withChance(double chance) {
		return new OutputItem(item.copy(), chance, rolls);
	}

	public OutputItem withRolls(IntProvider rolls) {
		return new OutputItem(item.copy(), chance, rolls);
	}

	public OutputItem withRolls(int min, int max) {
		return withRolls(UniformInt.of(min, max));
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

	public boolean isEmpty() {
		return this == EMPTY;
	}

	@Override
	public Object replaceOutput(KubeRecipe recipe, ReplacementMatch match, OutputReplacement original) {
		if (original instanceof OutputItem o) {
			var replacement = new OutputItem(item.copy(), o.chance, o.rolls);
			replacement.item.setCount(o.getCount());
			return replacement;
		}

		return new OutputItem(item.copy(), Double.NaN, null);
	}

	@Deprecated
	public InputItem ignoreNBT(Context cx) {
		var console = ConsoleJS.getCurrent(cx);
		console.warn("You don't need to call .ignoreNBT() anymore, all item ingredients ignore NBT by default!");
		return InputItem.of(item.getItem().kjs$asIngredient(), item.getCount());
	}
}
