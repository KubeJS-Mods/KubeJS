package dev.latvian.kubejs.crafting;

import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;

import java.util.Map;

/**
 * @author LatvianModder
 */
public abstract class PulverizerRecipeEventJS<T extends PulverizerRecipeEventJS.PulverizerRecipe> extends RecipeEventBaseJS<T>
{
	public abstract static class PulverizerRecipe implements RecipeJS
	{
		public IngredientJS input;
		public ItemStackJS output;
		public ItemStackJS secondaryOutput;
		public float secondaryOutputChance;
		public float power;

		public PulverizerRecipe()
		{
			input = EmptyItemStackJS.INSTANCE;
			output = EmptyItemStackJS.INSTANCE;
			secondaryOutput = EmptyItemStackJS.INSTANCE;
			secondaryOutputChance = 0.1F;
			power = 1F;
		}

		@Override
		public PulverizerRecipe set(Map<String, Object> properties)
		{
			if (properties.containsKey("input"))
			{
				input(properties.get("input"));
			}

			if (properties.containsKey("output"))
			{
				output(properties.get("output"));
			}

			if (properties.containsKey("secondary"))
			{
				Object s = properties.get("secondary");

				if (s instanceof Map && ((Map) s).containsKey("chance") && ((Map) s).containsKey("item"))
				{
					secondary(((Map) s).get("item"), ((Number) ((Map) s).get("chance")).floatValue());
				}
				else
				{
					secondary(s);
				}
			}

			if (properties.get("power") instanceof Number)
			{
				power(((Number) properties.get("power")).floatValue());
			}

			return this;
		}

		public PulverizerRecipe input(Object in)
		{
			input = IngredientJS.of(in);
			return this;
		}

		public PulverizerRecipe output(Object out)
		{
			output = ItemStackJS.of(out);
			return this;
		}

		public PulverizerRecipe secondary(Object out, float chance)
		{
			secondaryOutput = ItemStackJS.of(out);
			secondaryOutputChance = chance;
			return this;
		}

		public PulverizerRecipe secondary(Object out)
		{
			return secondary(out, 0.1F);
		}

		public PulverizerRecipe power(float relativePower)
		{
			power = relativePower;
			return this;
		}
	}

	public PulverizerRecipeEventJS(String m)
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

	@Override
	public void remove(Object output)
	{
		IngredientJS ingredient = IngredientJS.of(output);
		removePrimary(ingredient);
		removeSecondary(ingredient);
	}

	public void removePrimary(Object output)
	{
	}

	public void removeSecondary(Object output)
	{
	}
}