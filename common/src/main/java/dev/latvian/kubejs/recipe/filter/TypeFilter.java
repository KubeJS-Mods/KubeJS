package dev.latvian.kubejs.recipe.filter;

import dev.latvian.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

/**
 * @author LatvianModder
 */
public class TypeFilter implements RecipeFilter
{
	private final String type;

	public TypeFilter(String t)
	{
		type = t;

		if (RecipeJS.itemErrors && !Registry.RECIPE_SERIALIZER.containsKey(new ResourceLocation(type)))
		{
			throw new RecipeExceptionJS("Type '" + type + "' doesn't exist!").error();
		}
	}

	@Override
	public boolean test(RecipeJS r)
	{
		return r.type.toString().equals(type);
	}

	@Override
	public String toString()
	{
		return "TypeFilter{" +
				"type='" + type + '\'' +
				'}';
	}
}
