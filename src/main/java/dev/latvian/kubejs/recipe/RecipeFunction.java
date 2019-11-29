package dev.latvian.kubejs.recipe;

import dev.latvian.kubejs.recipe.type.RecipeJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerJS;
import jdk.nashorn.api.scripting.AbstractJSObject;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class RecipeFunction extends AbstractJSObject
{
	private final String id;
	private final List<RecipeJS> recipes;
	private final RecipeProviderJS provider;

	public RecipeFunction(String i, List<RecipeJS> r, RecipeProviderJS p)
	{
		id = i;
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
			if (ServerJS.instance.debugLog)
			{
				List<Object> list = new ArrayList<>();

				for (Object o : args)
				{
					list.add(String.valueOf(list));
				}

				ScriptType.SERVER.console.error("Failed to create recipe with type '" + id + "' from args " + list);
			}

			return RecipeJS.ERROR;
		}

		recipes.add(recipe);
		recipe.id = new ResourceLocation("kubejs", "generated_" + recipes.size());
		recipe.group = "";

		if (ServerJS.instance.debugLog)
		{
			ScriptType.SERVER.console.info("Added '" + id + "' recipe: " + recipe.toJson());
		}

		return recipe;
	}
}