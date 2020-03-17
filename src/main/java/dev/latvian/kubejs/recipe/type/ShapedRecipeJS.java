package dev.latvian.kubejs.recipe.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class ShapedRecipeJS extends RecipeJS
{
	private final List<String> pattern = new ArrayList<>();
	private final Map<String, IngredientJS> key = new HashMap<>();
	private ItemStackJS result = EmptyItemStackJS.INSTANCE;

	@Override
	public void create(ListJS args)
	{
		if (args.size() != 3)
		{
			throw new RecipeExceptionJS("Shaped recipe requires 3 arguments - result, pattern and keys!");
		}

		if (!(args.get(2) instanceof Map))
		{
			throw new RecipeExceptionJS("Shaped recipe pattern is empty!");
		}

		result = ItemStackJS.of(args.get(0));

		if (result.isEmpty())
		{
			throw new RecipeExceptionJS("Shaped recipe result " + args.get(0) + " is not a valid item!");
		}

		ListJS pattern1 = ListJS.orSelf(args.get(1));

		if (pattern1.isEmpty())
		{
			throw new RecipeExceptionJS("Shaped recipe pattern is empty!");
		}

		for (Object p : pattern1)
		{
			pattern.add(String.valueOf(p));
		}

		MapJS key1 = MapJS.of(args.get(2));

		if (key1 == null || key1.isEmpty())
		{
			throw new RecipeExceptionJS("Shaped recipe key map is empty!");
		}

		for (String k : key1.keySet())
		{
			IngredientJS i = IngredientJS.of(key1.get(k));

			if (!i.isEmpty())
			{
				key.put(k, i);
			}
			else
			{
				throw new RecipeExceptionJS("Shaped recipe ingredient " + key1.get(k) + " with key '" + k + "' is not a valid ingredient!");
			}
		}
	}

	@Override
	public void deserialize()
	{
		result = ItemStackJS.resultFromRecipeJson(json.get("result"));

		if (result.isEmpty())
		{
			throw new RecipeExceptionJS("Shaped recipe result " + json.get("result") + " is not a valid item!");
		}

		for (JsonElement e : json.get("pattern").getAsJsonArray())
		{
			pattern.add(e.getAsString());
		}

		if (pattern.isEmpty())
		{
			throw new RecipeExceptionJS("Shaped recipe pattern is empty!");
		}

		for (Map.Entry<String, JsonElement> entry : json.get("key").getAsJsonObject().entrySet())
		{
			IngredientJS i = IngredientJS.ingredientFromRecipeJson(entry.getValue());

			if (!i.isEmpty())
			{
				key.put(entry.getKey(), i);
			}
			else
			{
				throw new RecipeExceptionJS("Shaped recipe ingredient " + entry.getValue() + " with key '" + entry.getKey() + "' is not a valid ingredient!");
			}
		}

		if (key.isEmpty())
		{
			throw new RecipeExceptionJS("Shaped recipe key map is empty!");
		}
	}

	@Override
	public void serialize()
	{
		JsonArray patternJson = new JsonArray();

		for (String s : pattern)
		{
			patternJson.add(s);
		}

		json.add("pattern", patternJson);

		JsonObject keyJson = new JsonObject();

		for (Map.Entry<String, IngredientJS> entry : key.entrySet())
		{
			keyJson.add(entry.getKey(), entry.getValue().toJson());
		}

		json.add("key", keyJson);
		json.add("result", result.getResultJson());
	}

	@Override
	public Collection<IngredientJS> getInput()
	{
		return key.values();
	}

	@Override
	public boolean replaceInput(Object i, Object with)
	{
		boolean changed = false;

		for (Map.Entry<String, IngredientJS> entry : new ArrayList<>(key.entrySet()))
		{
			if (entry.getValue().anyStackMatches(IngredientJS.of(i)))
			{
				key.put(entry.getKey(), IngredientJS.of(with));
				changed = true;
				save();
			}
		}

		return changed;
	}

	@Override
	public Collection<ItemStackJS> getOutput()
	{
		return Collections.singleton(result);
	}

	@Override
	public boolean replaceOutput(Object i, Object with)
	{
		if (IngredientJS.of(i).test(result))
		{
			result = ItemStackJS.of(with).count(result.getCount());
			save();
			return true;
		}

		return false;
	}
}