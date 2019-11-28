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
public class CookingRecipeJS extends RecipeJS
{
	public enum Type
	{
		SMELTING("smelting", IRecipeSerializer.SMELTING),
		BLASTING("blasting", IRecipeSerializer.BLASTING),
		SMOKING("smoking", IRecipeSerializer.SMOKING),
		CAMPFIRE("campfire", IRecipeSerializer.CAMPFIRE_COOKING);

		public final String name;
		public final IRecipeSerializer serializer;
		public final RecipeProviderJS provider;
		public final RecipeDeserializerJS deserializer;

		Type(String n, IRecipeSerializer s)
		{
			name = n;
			serializer = s;
			provider = args -> {
				if (args.length == 2)
				{
					CookingRecipeJS recipe = new CookingRecipeJS(Type.this);
					recipe.result = ItemStackJS.of(args[0]);
					recipe.ingredient = IngredientJS.of(args[1]);

					if (!recipe.result.isEmpty() && !recipe.ingredient.isEmpty())
					{
						return recipe;
					}
				}

				return null;
			};

			deserializer = json -> {
				CookingRecipeJS recipe = new CookingRecipeJS(Type.this);
				recipe.result = ItemStackJS.fromRecipeJson(json.get("result"));
				recipe.ingredient = IngredientJS.fromRecipeJson(json.get("ingredient"));
				return recipe.result.isEmpty() || recipe.ingredient.isEmpty() ? null : recipe;
			};
		}
	}

	public final Type cookingType;
	public IngredientJS ingredient = EmptyItemStackJS.INSTANCE;
	public ItemStackJS result = EmptyItemStackJS.INSTANCE;
	public float experience = 0.1F;

	public CookingRecipeJS(Type c)
	{
		cookingType = c;
	}

	@Override
	public IRecipeSerializer getSerializer()
	{
		return cookingType.serializer;
	}

	@Override
	public JsonObject toJson()
	{
		JsonObject json = create();
		json.add("ingredient", ingredient.toIngredientJson());
		json.add("result", result.toRecipeResultJson());
		json.addProperty("experience", experience);
		return json;
	}

	public CookingRecipeJS xp(float xp)
	{
		experience = xp;
		return this;
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