package dev.latvian.kubejs.recipe;

import dev.latvian.kubejs.recipe.type.RecipeJS;
import jdk.nashorn.api.scripting.AbstractJSObject;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class RecipeFunction extends AbstractJSObject
{
	private List<RecipeJS> recipes;
	private RecipeProviderJS provider;

	public RecipeFunction(List<RecipeJS> r, RecipeProviderJS p)
	{
		provider = p;
		recipes = r;
	}

	@Override
	@Nullable
	public RecipeJS call(Object thiz, Object... args)
	{
		RecipeJS recipe = provider.create(args);

		if (recipe == null)
		{
			return RecipeJS.ERROR;
		}

		recipes.add(recipe);
		recipe.id = new ResourceLocation("kubejs", "generated_" + recipes.size());
		recipe.group = recipe.id.toString();
		return recipe;
	}
}