package dev.latvian.kubejs.recipe.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import jdk.nashorn.api.scripting.JSObject;
import net.minecraft.item.crafting.IRecipeSerializer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class ShapedRecipeJS extends RecipeJS
{
	public static final RecipeTypeJS TYPE = new RecipeTypeJS(IRecipeSerializer.CRAFTING_SHAPED)
	{
		@Nullable
		@Override
		public RecipeJS create(Object[] args)
		{
			if (args.length == 3 && args[1] instanceof JSObject && args[2] instanceof JSObject)
			{
				JSObject pattern = (JSObject) args[1];
				JSObject key = (JSObject) args[2];

				if (pattern.isArray() && !key.isArray())
				{
					ShapedRecipeJS recipe = new ShapedRecipeJS();
					recipe.result = ItemStackJS.of(args[0]);

					for (Object p : pattern.values())
					{
						recipe.pattern.add(String.valueOf(p));
					}

					for (String k : key.keySet())
					{
						recipe.key.put(k, IngredientJS.of(key.getMember(k)));
					}

					if (!recipe.result.isEmpty() && !recipe.pattern.isEmpty() && !recipe.key.isEmpty())
					{
						return recipe;
					}
				}
			}

			return null;
		}

		@Nullable
		@Override
		public RecipeJS create(JsonObject json)
		{
			ShapedRecipeJS recipe = new ShapedRecipeJS();

			for (JsonElement e : json.get("pattern").getAsJsonArray())
			{
				recipe.pattern.add(e.getAsString());
			}

			for (Map.Entry<String, JsonElement> entry : json.get("key").getAsJsonObject().entrySet())
			{
				IngredientJS i = IngredientJS.fromRecipeJson(entry.getValue());

				if (!i.isEmpty())
				{
					recipe.key.put(entry.getKey(), i);
				}
			}

			recipe.result = ItemStackJS.fromRecipeJson(json.get("result"));
			return recipe.result.isEmpty() || recipe.pattern.isEmpty() || recipe.key.isEmpty() ? null : recipe;
		}
	};

	public final List<String> pattern = new ArrayList<>();
	public final Map<String, IngredientJS> key = new HashMap<>();
	public ItemStackJS result = EmptyItemStackJS.INSTANCE;

	@Override
	public RecipeTypeJS getType()
	{
		return TYPE;
	}

	@Override
	public JsonObject toJson()
	{
		JsonObject json = create();

		JsonArray patternJson = new JsonArray();

		for (String s : pattern)
		{
			patternJson.add(s);
		}

		json.add("pattern", patternJson);

		JsonObject keyJson = new JsonObject();

		for (Map.Entry<String, IngredientJS> entry : key.entrySet())
		{
			keyJson.add(entry.getKey(), entry.getValue().getJson());
		}

		json.add("key", keyJson);

		json.add("result", result.getResultJson());
		return json;
	}

	@Override
	public boolean hasInput(IngredientJS ingredient)
	{
		for (IngredientJS in : key.values())
		{
			if (in.anyStackMatches(ingredient))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean hasOutput(IngredientJS ingredient)
	{
		return ingredient.test(result);
	}
}