package dev.latvian.kubejs.recipe;

import java.util.function.Supplier;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * @author LatvianModder
 */
public class RecipeTypeJS
{
	public final RecipeSerializer<?> serializer;
	public final Supplier<RecipeJS> factory;
	private final String string;

	public RecipeTypeJS(RecipeSerializer<?> s, Supplier<RecipeJS> f)
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