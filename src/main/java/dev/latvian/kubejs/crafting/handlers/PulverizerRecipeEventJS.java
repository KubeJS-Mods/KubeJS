package dev.latvian.kubejs.crafting.handlers;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;

import java.util.Map;

/**
 * @author LatvianModder
 */
public abstract class PulverizerRecipeEventJS<T extends PulverizerRecipeEventJS.PulverizerRecipe> extends EventJS
{
	public abstract static class PulverizerRecipe
	{
		public ItemStackJS input;
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

		public PulverizerRecipe set(Map<String, Object> properties)
		{
			if (properties.containsKey("in"))
			{
				in(properties.get("in"));
			}

			if (properties.containsKey("out"))
			{
				out(properties.get("out"));
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

		public PulverizerRecipe in(Object in)
		{
			input = ItemStackJS.of(in);
			return this;
		}

		public PulverizerRecipe out(Object out)
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

		public void add()
		{
		}
	}

	public final String mod;

	public PulverizerRecipeEventJS(String m)
	{
		mod = m;
	}

	protected abstract T createRecipe();

	public final T create(Object in, Object out)
	{
		T recipe = createRecipe();
		recipe.in(in);
		recipe.out(out);
		return recipe;
	}

	public final void add(Map<String, Object> properties)
	{
		T recipe = createRecipe();
		recipe.set(properties);
		recipe.add();
	}

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

	public void removeInput(Object input)
	{
	}
}