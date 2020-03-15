package dev.latvian.kubejs.recipe.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.ListJS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class CustomRecipeJS extends RecipeJS
{
	public static final Supplier<RecipeJS> FACTORY = CustomRecipeJS::new;

	private List<IngredientJS> input;
	private List<ItemStackJS> output;
	private String inputKey;
	private int inputType;
	private String outputKey;
	private int outputType;

	public CustomRecipeJS()
	{
		input = new ArrayList<>(1);
		output = new ArrayList<>(1);
		inputKey = "";
		inputType = -1;
		outputKey = "";
		outputType = -1;
	}

	@Override
	public void create(ListJS args)
	{
		throw new RecipeExceptionJS("Can't create custom recipe for type " + id + "!");
	}

	private boolean addInput(String k)
	{
		JsonElement e = json.get(k);

		if (e == null || e.isJsonNull())
		{
			return false;
		}

		if (e.isJsonArray())
		{
			for (JsonElement e1 : e.getAsJsonArray())
			{
				IngredientJS i = IngredientJS.ingredientFromRecipeJson(e1);

				if (!i.isEmpty())
				{
					input.add(i);
				}
			}

			inputKey = k;
			inputType = 1;
			return true;
		}

		IngredientJS i = IngredientJS.ingredientFromRecipeJson(e);

		if (!i.isEmpty())
		{
			input.add(i);
			inputKey = k;
			inputType = 0;
			return true;
		}

		return false;
	}

	private boolean addOutput(String k)
	{
		JsonElement e = json.get(k);

		if (e == null || e.isJsonNull())
		{
			return false;
		}

		if (e.isJsonArray())
		{
			for (JsonElement e1 : e.getAsJsonArray())
			{
				ItemStackJS i = ItemStackJS.resultFromRecipeJson(e1);

				if (!i.isEmpty())
				{
					output.add(i);
				}
			}

			outputKey = k;
			outputType = 1;
			return true;
		}

		ItemStackJS i = ItemStackJS.resultFromRecipeJson(e);

		if (!i.isEmpty())
		{
			if (e.isJsonPrimitive())
			{
				outputType = 2;

				if (json.has("count"))
				{
					i.count(json.get("count").getAsInt());
				}
			}
			else
			{
				outputType = 0;
			}

			outputKey = k;
			output.add(i);
			return true;
		}

		return false;
	}

	@Override
	public void deserialize()
	{
		input.clear();
		output.clear();
		inputKey = "";
		inputType = -1;
		outputKey = "";
		outputType = -1;

		if (originalRecipe == null || originalRecipe.isDynamic())
		{
			return;
		}

		try
		{
			if (!addInput("ingredient")
					&& !addInput("ingredients")
					&& !addInput("in")
					&& !addInput("input")
					&& !addInput("inputs")
					&& !addInput("itemInput")
					&& !addInput("infusionInput")
			)
			{
				ScriptType.SERVER.console.logger.debug("! " + this + ": Couldn't find any input items!");
			}
		}
		catch (Exception ex)
		{
		}

		try
		{
			if (!addOutput("result")
					&& !addOutput("results")
					&& !addOutput("out")
					&& !addOutput("output")
					&& !addOutput("outputs")
					&& !addOutput("itemOutput")
					&& !addOutput("mainOutput")
					&& !addOutput("secondaryOutput")
			)
			{
				ScriptType.SERVER.console.logger.debug("! " + this + ": Couldn't find any output items!");
			}
		}
		catch (Exception ex)
		{
		}
	}

	@Override
	public void serialize()
	{
		if (inputType != -1 && !inputKey.isEmpty())
		{
			if (inputType == 1)
			{
				JsonArray a = new JsonArray();

				for (IngredientJS in : input)
				{
					a.add(in.toJson());
				}

				json.add(inputKey, a);
			}
			else if (inputType == 0)
			{
				json.add(inputKey, (input.isEmpty() ? EmptyItemStackJS.INSTANCE : input.get(0)).toJson());
			}
		}

		if (outputType != -1 && !outputKey.isEmpty())
		{
			if (outputType == 1)
			{
				JsonArray a = new JsonArray();

				for (ItemStackJS in : output)
				{
					a.add(in.getResultJson());
				}

				json.add(outputKey, a);
			}
			else if (outputType == 2)
			{
				json.addProperty(outputKey, (output.isEmpty() ? EmptyItemStackJS.INSTANCE : output.get(0)).getId().toString());
				json.addProperty("count", (output.isEmpty() ? EmptyItemStackJS.INSTANCE : output.get(0)).getCount());
			}
			else if (outputType == 0)
			{
				json.add(outputKey, (output.isEmpty() ? EmptyItemStackJS.INSTANCE : output.get(0)).getResultJson());
			}
		}
	}

	@Override
	public Collection<IngredientJS> getInput()
	{
		return input;
	}

	@Override
	public boolean replaceInput(Object i, Object with)
	{
		boolean changed = false;

		for (int j = 0; j < input.size(); j++)
		{
			if (input.get(j).anyStackMatches(IngredientJS.of(i)))
			{
				input.set(j, IngredientJS.of(with));
				changed = true;
				save();
			}
		}

		return changed;
	}

	@Override
	public Collection<ItemStackJS> getOutput()
	{
		return output;
	}

	@Override
	public boolean replaceOutput(Object i, Object with)
	{
		boolean changed = false;

		for (int j = 0; j < output.size(); j++)
		{
			if (IngredientJS.of(i).test(output.get(j)))
			{
				output.set(j, ItemStackJS.of(with));
				changed = true;
				save();
			}
		}

		return changed;
	}
}