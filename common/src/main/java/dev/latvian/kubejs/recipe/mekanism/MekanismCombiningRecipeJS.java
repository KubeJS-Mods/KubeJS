package dev.latvian.kubejs.recipe.mekanism;

import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.util.ListJS;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * @author LatvianModder
 */
public class MekanismCombiningRecipeJS extends RecipeJS
{
	public int inputAmount1, inputAmount2;

	@Override
	public void create(ListJS args)
	{
		if (args.size() < 3)
		{
			throw new RecipeExceptionJS("Mekanism combining recipe has to have 3 arguments - ouptut, input 1, input 2!");
		}

		ItemStackJS output = ItemStackJS.of(args.get(0));

		if (output.isEmpty())
		{
			throw new RecipeExceptionJS("Mekanism combining recipe result can't be empty!");
		}

		outputItems.add(output);

		ListJS in = ListJS.orSelf(args.get(1));

		IngredientJS i1 = IngredientJS.of(args.get(1));

		if (!i1.isEmpty())
		{
			int c = i1.getCount();

			if (c > 1)
			{
				inputItems.add(i1.count(1));
				inputAmount1 = c;
			}
			else
			{
				inputItems.add(i1);
				inputAmount1 = 1;
			}
		}
		else
		{
			throw new RecipeExceptionJS("Mekanism combining recipe ingredient #1 " + args.get(1) + " is not a valid ingredient!");
		}

		IngredientJS i2 = IngredientJS.of(args.get(1));

		if (!i2.isEmpty())
		{
			int c = i2.getCount();

			if (c > 1)
			{
				inputItems.add(i2.count(1));
				inputAmount2 = c;
			}
			else
			{
				inputItems.add(i2);
				inputAmount2 = 1;
			}
		}
		else
		{
			throw new RecipeExceptionJS("Mekanism combining recipe ingredient #2 " + args.get(2) + " is not a valid ingredient!");
		}
	}

	@Override
	public void deserialize()
	{
		ItemStackJS output = ItemStackJS.resultFromRecipeJson(json.get("output"));

		if (output.isEmpty())
		{
			throw new RecipeExceptionJS("Mekanism machine recipe result can't be empty!");
		}

		outputItems.add(output);

		List<Pair<IngredientJS, Integer>> list1 = MekanismMachineRecipeJS.deserializeIngredient(json.get("mainInput"));

		if (list1.isEmpty())
		{
			throw new RecipeExceptionJS("Mekanism machine recipe ingredient " + json.get("mainInput") + " is not a valid ingredient!");
		}

		List<Pair<IngredientJS, Integer>> list2 = MekanismMachineRecipeJS.deserializeIngredient(json.get("extraInput"));

		if (list2.isEmpty())
		{
			throw new RecipeExceptionJS("Mekanism machine recipe ingredient " + json.get("extraInput") + " is not a valid ingredient!");
		}

		inputItems.add(list1.get(0).getLeft());
		inputAmount1 = list1.get(0).getRight();

		inputItems.add(list2.get(0).getLeft());
		inputAmount2 = list2.get(0).getRight();
	}

	@Override
	public void serialize()
	{
		json.add("mainInput", MekanismMachineRecipeJS.serializeIngredient(inputItems.get(0), inputAmount1));
		json.add("extraInput", MekanismMachineRecipeJS.serializeIngredient(inputItems.get(1), inputAmount2));
		json.add("output", outputItems.get(0).toResultJson());
	}
}