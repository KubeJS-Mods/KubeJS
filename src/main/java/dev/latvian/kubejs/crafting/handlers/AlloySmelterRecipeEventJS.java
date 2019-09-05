package dev.latvian.kubejs.crafting.handlers;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;

import java.util.Map;

/**
 * @author LatvianModder
 */
public abstract class AlloySmelterRecipeEventJS<T extends AlloySmelterRecipeEventJS.AlloySmelterRecipe> extends EventJS
{
	public abstract static class AlloySmelterRecipe
	{
		public ItemStackJS input;
		public ItemStackJS output;
		public ItemStackJS secondaryOutput;
		public float secondaryOutputChance;
		public float power;

		public AlloySmelterRecipe()
		{
			input = EmptyItemStackJS.INSTANCE;
			output = EmptyItemStackJS.INSTANCE;
			secondaryOutput = EmptyItemStackJS.INSTANCE;
			secondaryOutputChance = 0.1F;
			power = 1F;
		}

		public AlloySmelterRecipe set(Map<String, Object> properties)
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

		public AlloySmelterRecipe in(Object in)
		{
			input = ItemStackJS.of(in);
			return this;
		}

		public AlloySmelterRecipe out(Object out)
		{
			output = ItemStackJS.of(out);
			return this;
		}

		public AlloySmelterRecipe secondary(Object out, float chance)
		{
			secondaryOutput = ItemStackJS.of(out);
			secondaryOutputChance = chance;
			return this;
		}

		public AlloySmelterRecipe secondary(Object out)
		{
			return secondary(out, 0.1F);
		}

		public AlloySmelterRecipe power(float relativePower)
		{
			power = relativePower;
			return this;
		}

		public void add()
		{
		}
	}

	public final String mod;

	public AlloySmelterRecipeEventJS(String m)
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

	public void remove(IngredientJS output)
	{
	}

	public void removeInput(IngredientJS input)
	{
	}
}