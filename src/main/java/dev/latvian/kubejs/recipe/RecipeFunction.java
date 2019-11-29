package dev.latvian.kubejs.recipe;

import dev.latvian.kubejs.recipe.type.CustomRecipeJS;
import dev.latvian.kubejs.recipe.type.RecipeJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.util.JsonUtilsJS;
import jdk.nashorn.api.scripting.AbstractJSObject;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class RecipeFunction extends AbstractJSObject
{
	public final RecipeTypeJS type;
	private final List<RecipeJS> recipes;

	public RecipeFunction(RecipeTypeJS t, List<RecipeJS> r)
	{
		type = t;
		recipes = r;
	}

	@Override
	public RecipeJS call(Object thiz, Object... args)
	{
		if (args.length == 1 && args[0] instanceof Map)
		{
			CustomRecipeJS recipe = new CustomRecipeJS();
			recipes.add(recipe);
			recipe.id = new ResourceLocation("kubejs", "generated_" + recipes.size());
			recipe.group = "";
			recipe.data = JsonUtilsJS.of(args[0]).getAsJsonObject();
			recipe.typeId = type.id;

			if (ServerJS.instance.debugLog)
			{
				ScriptType.SERVER.console.info("Added custom recipe: " + recipe.toJson());
			}

			return recipe;
		}

		RecipeJS recipe = null;

		try
		{
			recipe = type.create(args);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		if (recipe == null)
		{
			if (ServerJS.instance.debugLog)
			{
				List<Object> list = new ArrayList<>();

				for (Object o : args)
				{
					list.add(String.valueOf(list));
				}

				ScriptType.SERVER.console.error("Failed to create recipe with type '" + type.id + "' from args " + list);
			}

			return RecipeJS.ERROR;
		}

		recipes.add(recipe);
		recipe.id = new ResourceLocation("kubejs", "generated_" + recipes.size());
		recipe.group = "";

		if (ServerJS.instance.debugLog)
		{
			ScriptType.SERVER.console.info("Added '" + recipe.getType().id + "' recipe: " + recipe.toJson());
		}

		return recipe;
	}

	@Override
	public String toString()
	{
		return type.id.toString();
	}
}