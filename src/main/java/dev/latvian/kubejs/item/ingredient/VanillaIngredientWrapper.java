package dev.latvian.kubejs.item.ingredient;

import dev.latvian.kubejs.item.ItemStackJS;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeItemHelper;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author LatvianModder
 */
public class VanillaIngredientWrapper extends Ingredient
{
	public final IngredientJS ingredientJS;
	private ItemStack[] matchingStacksCache;
	private IntList validItemStacksPackedCache;

	public VanillaIngredientWrapper(IngredientJS i)
	{
		super(Stream.of());
		ingredientJS = i;
	}

	@Override
	public ItemStack[] getMatchingStacks()
	{
		if (matchingStacksCache == null)
		{
			Set<ItemStackJS> set = ingredientJS.getStacks();
			matchingStacksCache = new ItemStack[set.size()];
			int i = 0;

			for (ItemStackJS stack : set)
			{
				matchingStacksCache[i] = stack.getItemStack();
				i++;
			}
		}

		return matchingStacksCache;
	}

	@Override
	public boolean test(@Nullable ItemStack stack)
	{
		return stack != null && !stack.isEmpty() && ingredientJS.testVanilla(stack);
	}

	@Override
	public IntList getValidItemStacksPacked()
	{
		if (validItemStacksPackedCache == null)
		{
			validItemStacksPackedCache = new IntArrayList(getMatchingStacks().length);

			for (ItemStack itemstack : getMatchingStacks())
			{
				validItemStacksPackedCache.add(RecipeItemHelper.pack(itemstack));
			}

			validItemStacksPackedCache.sort(IntComparators.NATURAL_COMPARATOR);
		}

		return validItemStacksPackedCache;
	}

	@Override
	protected void invalidate()
	{
		matchingStacksCache = null;
		validItemStacksPackedCache = null;
	}
}