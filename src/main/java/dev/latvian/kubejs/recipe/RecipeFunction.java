package dev.latvian.kubejs.recipe;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.recipe.minecraft.CustomRecipeJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.WrappedJS;
import jdk.nashorn.api.scripting.AbstractJSObject;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class RecipeFunction extends AbstractJSObject implements WrappedJS
{
	private final RecipeEventJS event;
	public final ResourceLocation typeID;
	public final RecipeTypeJS type;

	public RecipeFunction(RecipeEventJS e, ResourceLocation id, @Nullable RecipeTypeJS t)
	{
		event = e;
		typeID = id;
		type = t;
	}

	@Override
	public RecipeJS call(Object thiz, Object... args0)
	{
		try
		{
			if (type == null)
			{
				throw new RecipeExceptionJS("Unknown recipe type!");
			}

			ListJS args = ListJS.of(args0);

			if (args == null || args.isEmpty())
			{
				throw new RecipeExceptionJS("Recipe requires at least one argument!");
			}
			else if (type.isCustom() && args.size() != 1)
			{
				throw new RecipeExceptionJS("Custom recipe has to use a single json object argument!");
			}

			if (args.size() == 1)
			{
				MapJS map = MapJS.of(args.get(0));

				if (map != null)
				{
					RecipeJS recipe = type.factory.get();
					recipe.type = type;
					recipe.json = map.toJson();
					recipe.deserialize();
					return event.addRecipe(recipe, type, args);
				}
				else
				{
					throw new RecipeExceptionJS("One argument recipes have to be a JSON object!");
				}
			}

			RecipeJS recipe = type.factory.get();
			recipe.type = type;
			recipe.json = new JsonObject();
			recipe.create(args);
			return event.addRecipe(recipe, type, args);
		}
		catch (RecipeExceptionJS ex)
		{
			ScriptType.SERVER.console.warn("Failed to create recipe for type '" + typeID + "': " + ex);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return new CustomRecipeJS();
	}

	@Override
	public String toString()
	{
		return typeID.toString();
	}
}