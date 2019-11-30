package dev.latvian.kubejs.recipe;

import dev.latvian.kubejs.recipe.type.CustomRecipeJS;
import dev.latvian.kubejs.recipe.type.RecipeJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.JsonUtilsJS;
import dev.latvian.kubejs.util.UtilsJS;
import jdk.nashorn.api.scripting.AbstractJSObject;
import net.minecraft.util.ResourceLocation;

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
	public RecipeJS call(Object thiz, Object... args0)
	{
		List args1 = UtilsJS.getNormalizedList(args0);

		if (args1 == null || args1.isEmpty())
		{
			return new RecipeErrorJS("Recipe requires at least one argument!");
		}

		Object[] args = args1.toArray();

		if (args.length == 1 && args[0] instanceof Map)
		{
			CustomRecipeJS recipe = new CustomRecipeJS();
			recipes.add(recipe);
			recipe.id = new ResourceLocation("kubejs", "generated_" + recipes.size());
			recipe.group = "";
			recipe.data = JsonUtilsJS.of(args[0]).getAsJsonObject();
			recipe.typeId = type.id;
			ScriptType.SERVER.console.info("Added '" + type.id + "' recipe: " + recipe.toJson());
			return recipe;
		}

		RecipeJS recipe;

		try
		{
			recipe = type.create(args);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			recipe = new RecipeErrorJS(ex.toString());
		}

		if (recipe instanceof RecipeErrorJS)
		{
			ScriptType.SERVER.console.error("Error creating '" + type.id + "' recipe: " + ((RecipeErrorJS) recipe).message);
			ScriptType.SERVER.console.error(args1);
			ScriptType.SERVER.console.error("");
			return recipe;
		}

		recipes.add(recipe);
		recipe.id = new ResourceLocation("kubejs", "generated_" + recipes.size());
		recipe.group = "";
		ScriptType.SERVER.console.info("Added '" + recipe.getType().id + "' recipe: " + recipe.toJson());
		return recipe;
	}

	@Override
	public String toString()
	{
		return type.id.toString();
	}
}