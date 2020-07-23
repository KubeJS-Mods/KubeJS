package dev.latvian.kubejs.recipe;

import net.minecraft.item.crafting.IRecipeSerializer;

import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class RecipeTypeJS
{
	public final IRecipeSerializer<?> serializer;
	public final Supplier<RecipeJS> factory;
	private final String string;

	public RecipeTypeJS(IRecipeSerializer<?> s, Supplier<RecipeJS> f)
	{
		serializer = s;
		factory = f;
		string = s.getRegistryName().toString();
	}

	public boolean isCustom()
	{
		return false;
	}

	@Override
	public String toString()
	{
		return string;
	}

	@Override
	public int hashCode()
	{
		return string.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		return string.equals(obj.toString());
	}
}