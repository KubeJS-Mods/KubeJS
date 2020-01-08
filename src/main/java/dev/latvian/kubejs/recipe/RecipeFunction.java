package dev.latvian.kubejs.recipe;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.recipe.type.RecipeJS;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.WrappedJS;
import jdk.nashorn.api.scripting.AbstractJSObject;

/**
 * @author LatvianModder
 */
public class RecipeFunction extends AbstractJSObject implements WrappedJS
{
	private final RecipeEventJS event;
	public final RecipeTypeJS type;

	public RecipeFunction(RecipeEventJS e, RecipeTypeJS t)
	{
		event = e;
		type = t;
	}

	@Override
	public RecipeJS call(Object thiz, Object... args0)
	{
		ListJS args = ListJS.of(args0);

		if (args == null || args.isEmpty())
		{
			return new RecipeErrorJS("Recipe requires at least one argument!");
		}

		if (args.size() == 1)
		{
			MapJS map = MapJS.of(args.get(0));

			if (map != null)
			{
				JsonObject json = map.toJson();
				json.addProperty("type", type.id.toString());
				return event.addRecipe(type.create(json), type, args);
			}
			else
			{
				return new RecipeErrorJS("One argument recipes have to be a JSON object!");
			}
		}

		try
		{
			return event.addRecipe(type.create(args), type, args);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return event.addRecipe(new RecipeErrorJS(ex.toString()), type, args);
		}
	}

	@Override
	public String toString()
	{
		return type.id.toString();
	}
}