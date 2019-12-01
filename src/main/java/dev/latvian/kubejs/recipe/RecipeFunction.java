package dev.latvian.kubejs.recipe;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.recipe.type.RecipeJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerJS;
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
			JsonObject json = JsonUtilsJS.of(args[0]).getAsJsonObject();
			json.addProperty("type", type.id.toString());
			return add(type.create(json), args1);
		}

		try
		{
			return add(type.create(args), args1);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return add(new RecipeErrorJS(ex.toString()), args1);
		}
	}

	private RecipeJS add(RecipeJS recipe, List args1)
	{
		if (recipe instanceof RecipeErrorJS)
		{
			ScriptType.SERVER.console.error("Broken '" + type.id + "' recipe: " + ((RecipeErrorJS) recipe).message);
			ScriptType.SERVER.console.error(args1);
			ScriptType.SERVER.console.error("");
			return recipe;
		}

		recipe.id = new ResourceLocation(type.id.getNamespace(), "kubejs_generated_" + recipes.size());
		recipe.group = "";

		if (ServerJS.instance.logAddedRecipes)
		{
			ScriptType.SERVER.console.info("Added '" + type.id + "' recipe: " + recipe.toJson());
		}

		recipes.add(recipe);
		return recipe;
	}

	@Override
	public String toString()
	{
		return type.id.toString();
	}
}