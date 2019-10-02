package dev.latvian.kubejs.crafting;

import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

import java.util.Map;

/**
 * @author LatvianModder
 */
public class FurnaceRecipeEventJS extends RecipeEventBaseJS<FurnaceRecipeEventJS.FurnaceRecipe>
{
	public static class FurnaceRecipe implements RecipeJS
	{
		public IngredientJS input;
		public ItemStackJS output;
		public float experience;

		public FurnaceRecipe()
		{
			input = EmptyItemStackJS.INSTANCE;
			output = EmptyItemStackJS.INSTANCE;
			experience = 0.1F;
		}

		@Override
		public FurnaceRecipe set(Map<String, Object> properties)
		{
			if (properties.containsKey("input"))
			{
				input(properties.get("input"));
			}

			if (properties.containsKey("output"))
			{
				output(properties.get("output"));
			}

			if (properties.get("xp") instanceof Number)
			{
				xp(((Number) properties.get("xp")).floatValue());
			}

			return this;
		}

		public FurnaceRecipe input(Object in)
		{
			input = IngredientJS.of(in);
			return this;
		}

		public FurnaceRecipe output(Object out)
		{
			output = ItemStackJS.of(out);
			return this;
		}

		public FurnaceRecipe xp(float xp)
		{
			experience = xp;
			return this;
		}

		@Override
		public void add()
		{
			ItemStack out = output.getItemStack();

			for (ItemStackJS in : input.getStacks())
			{
				FurnaceRecipes.instance().addSmeltingRecipe(in.getItemStack(), out, experience);
			}
		}
	}

	public FurnaceRecipeEventJS(String m)
	{
		super(m);
	}

	@Override
	protected FurnaceRecipe createRecipe()
	{
		return new FurnaceRecipe();
	}

	public final FurnaceRecipe create(Object in, Object out)
	{
		FurnaceRecipe recipe = createRecipe();
		recipe.input(in);
		recipe.output(out);
		return recipe;
	}

	@Override
	public void remove(Object output)
	{
		FurnaceRecipes.instance().getSmeltingList().values().removeIf(IngredientJS.of(output).getVanillaPredicate());
	}

	@Override
	public void removeInput(Object input)
	{
		FurnaceRecipes.instance().getSmeltingList().keySet().removeIf(IngredientJS.of(input).getVanillaPredicate());
	}
}