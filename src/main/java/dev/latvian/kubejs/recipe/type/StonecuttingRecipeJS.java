package dev.latvian.kubejs.recipe.type;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeDeserializerJS;
import dev.latvian.kubejs.recipe.RecipeProviderJS;
import net.minecraft.item.crafting.IRecipeSerializer;

/**
 * @author LatvianModder
 */
public class StonecuttingRecipeJS extends RecipeJS
{
	public static final RecipeProviderJS PROVIDER = args -> {
		if (args.length == 2)
		{
			StonecuttingRecipeJS recipe = new StonecuttingRecipeJS();
			recipe.result = ItemStackJS.of(args[0]);
			recipe.ingredient = IngredientJS.of(args[1]);

			if (!recipe.result.isEmpty() && !recipe.ingredient.isEmpty())
			{
				return recipe;
			}
		}

		return null;
	};

	public static final RecipeDeserializerJS DESERIALIZER = json -> {
		StonecuttingRecipeJS recipe = new StonecuttingRecipeJS();
		recipe.result = ItemStackJS.fromRecipeJson(json.get("result"));
		recipe.ingredient = IngredientJS.fromRecipeJson(json.get("ingredient"));
		return recipe.result.isEmpty() || recipe.ingredient.isEmpty() ? null : recipe;
	};

	public IngredientJS ingredient = EmptyItemStackJS.INSTANCE;
	public ItemStackJS result = EmptyItemStackJS.INSTANCE;

	@Override
	public IRecipeSerializer getSerializer()
	{
		return IRecipeSerializer.STONECUTTING;
	}

	@Override
	public JsonObject toJson()
	{
		JsonObject json = create();
		json.add("ingredient", ingredient.toIngredientJson());
		json.addProperty("result", result.getId().toString());
		json.addProperty("count", result.getCount());
		return json;
	}

	@Override
	public boolean hasInput(IngredientJS i)
	{
		return ingredient.anyStackMatches(i);
	}

	@Override
	public boolean hasOutput(IngredientJS ingredient)
	{
		return ingredient.test(result);
	}
}