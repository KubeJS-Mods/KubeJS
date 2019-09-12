package dev.latvian.kubejs.crafting;

import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.util.UtilsJS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public abstract class AlloySmelterRecipeEventJS<T extends AlloySmelterRecipeEventJS.AlloySmelterRecipe> extends RecipeEventBaseJS<T>
{
	public abstract static class AlloySmelterRecipe extends RecipeBaseJS
	{
		public final List<IngredientJS> input;
		public ItemStackJS output;
		public ItemStackJS secondaryOutput;
		public float secondaryOutputChance;
		public float power;

		public AlloySmelterRecipe()
		{
			input = new ArrayList<>();
			output = EmptyItemStackJS.INSTANCE;
			secondaryOutput = EmptyItemStackJS.INSTANCE;
			secondaryOutputChance = 0.1F;
			power = 1F;
		}

		@Override
		public AlloySmelterRecipe set(Map<String, Object> properties)
		{
			Object in = properties.get("input");

			if (in != null)
			{
				for (Object o : UtilsJS.getList(in))
				{
					input(o);
				}
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

		public AlloySmelterRecipe input(Object in)
		{
			IngredientJS ingredient = IngredientJS.of(in);

			if (!ingredient.isEmpty())
			{
				input.add(ingredient);
			}

			return this;
		}

		public AlloySmelterRecipe output(Object out)
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
	}

	public AlloySmelterRecipeEventJS(String m)
	{
		super(m);
	}

	public final T create(Object in1, Object in2, Object out)
	{
		T recipe = createRecipe();
		recipe.input(in1);
		recipe.input(in2);
		recipe.output(out);
		return recipe;
	}

	public final T create(Collection<Object> in, Object out)
	{
		T recipe = createRecipe();

		for (Object o : in)
		{
			recipe.input(o);
		}

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