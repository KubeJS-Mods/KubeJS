package dev.latvian.kubejs.crafting;

import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;

import java.util.Map;

/**
 * @author LatvianModder
 */
public abstract class CompressorRecipeEventJS<T extends CompressorRecipeEventJS.CompressorRecipe> extends RecipeEventBaseJS<T>
{
	public abstract static class CompressorRecipe implements RecipeJS
	{
		public IngredientJS input;
		public ItemStackJS output;
		public float power;

		public CompressorRecipe()
		{
			input = EmptyItemStackJS.INSTANCE;
			output = EmptyItemStackJS.INSTANCE;
			power = 1F;
		}

		@Override
		public CompressorRecipe set(Map<String, Object> properties)
		{
			if (properties.containsKey("input"))
			{
				input(properties.get("input"));
			}

			if (properties.containsKey("output"))
			{
				output(properties.get("output"));
			}

			if (properties.get("power") instanceof Number)
			{
				power(((Number) properties.get("power")).floatValue());
			}

			return this;
		}

		public CompressorRecipe input(Object in)
		{
			input = IngredientJS.of(in);
			return this;
		}

		public CompressorRecipe output(Object out)
		{
			output = ItemStackJS.of(out);
			return this;
		}

		public CompressorRecipe power(float relativePower)
		{
			power = relativePower;
			return this;
		}
	}

	public CompressorRecipeEventJS(String m)
	{
		super(m);
	}

	public final T create(Object in, Object out)
	{
		T recipe = createRecipe();
		recipe.input(in);
		recipe.output(out);
		return recipe;
	}
}