package dev.latvian.kubejs.crafting.handlers;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;

import java.util.Map;

/**
 * @author LatvianModder
 */
public abstract class CompressorRecipeEventJS<T extends CompressorRecipeEventJS.CompressorRecipe> extends EventJS
{
	public abstract static class CompressorRecipe
	{
		public ItemStackJS input;
		public ItemStackJS output;
		public float power;

		public CompressorRecipe()
		{
			input = EmptyItemStackJS.INSTANCE;
			output = EmptyItemStackJS.INSTANCE;
			power = 1F;
		}

		public CompressorRecipe set(Map<String, Object> properties)
		{
			if (properties.containsKey("in"))
			{
				in(properties.get("in"));
			}

			if (properties.containsKey("out"))
			{
				out(properties.get("out"));
			}

			if (properties.get("power") instanceof Number)
			{
				power(((Number) properties.get("power")).floatValue());
			}

			return this;
		}

		public CompressorRecipe in(Object in)
		{
			input = ItemStackJS.of(in);
			return this;
		}

		public CompressorRecipe out(Object out)
		{
			output = ItemStackJS.of(out);
			return this;
		}

		public CompressorRecipe power(float relativePower)
		{
			power = relativePower;
			return this;
		}

		public void add()
		{
		}
	}

	public final String mod;

	public CompressorRecipeEventJS(String m)
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
	}

	public void removeInput(Object input)
	{
	}
}