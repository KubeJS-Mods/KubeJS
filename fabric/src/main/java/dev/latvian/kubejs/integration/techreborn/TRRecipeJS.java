package dev.latvian.kubejs.integration.techreborn;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.IngredientStackJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.util.ListJS;
import net.minecraft.resources.ResourceLocation;

/**
 * @author LatvianModder
 */
public class TRRecipeJS extends RecipeJS
{
	private static class DummyRebornIngredient implements IngredientJS
	{
		public static final ResourceLocation STACK_RECIPE_TYPE = new ResourceLocation("reborncore", "stack");
		public static final ResourceLocation FLUID_RECIPE_TYPE = new ResourceLocation("reborncore", "fluid");
		public static final ResourceLocation TAG_RECIPE_TYPE = new ResourceLocation("reborncore", "tag");
		public static final ResourceLocation WRAPPED_RECIPE_TYPE = new ResourceLocation("reborncore", "wrapped");

		public final ResourceLocation type;
		public final JsonObject json;

		public DummyRebornIngredient(ResourceLocation t, JsonObject o)
		{
			type = t;
			json = o;
		}

		@Override
		public boolean test(ItemStackJS stack)
		{
			return false;
		}

		@Override
		public JsonElement toJson()
		{
			return json;
		}
	}

	@Override
	public void create(ListJS args)
	{
		outputItems.addAll(parseResultItemList(args.get(0)));
		inputItems.addAll(parseIngredientItemList(args.get(1)));
		json.addProperty("power", 2);
		json.addProperty("time", 200);

		if (type.toString().equals("techreborn:blast_furnace"))
		{
			json.addProperty("heat", 1500);
		}
	}

	@Override
	public void deserialize()
	{
		outputItems.addAll(parseResultItemList(json.get("results")));
		inputItems.addAll(parseIngredientItemList(json.get("ingredients")));
	}

	public TRRecipeJS power(int i)
	{
		json.addProperty("power", i);
		save();
		return this;
	}

	public TRRecipeJS time(int i)
	{
		json.addProperty("time", i);
		save();
		return this;
	}

	public TRRecipeJS heat(int i)
	{
		json.addProperty("heat", i);
		save();
		return this;
	}

	@Override
	public void serialize()
	{
		if (serializeOutputs)
		{
			JsonArray array = new JsonArray();

			for (ItemStackJS out : outputItems)
			{
				array.add(out.toResultJson());
			}

			json.add("results", array);
		}

		if (serializeInputs)
		{
			JsonArray array = new JsonArray();

			for (IngredientJS in : inputItems)
			{
				array.add(in.toJson());
			}

			json.add("ingredients", array);
		}
	}

	@Override
	public IngredientJS parseIngredientItem(Object o, String key)
	{
		if (o instanceof JsonObject)
		{
			JsonObject json = (JsonObject) o;

			ResourceLocation type = DummyRebornIngredient.STACK_RECIPE_TYPE;

			if (json.has("fluid"))
			{
				type = DummyRebornIngredient.FLUID_RECIPE_TYPE;
			}
			else if (json.has("tag"))
			{
				type = DummyRebornIngredient.TAG_RECIPE_TYPE;
			}
			else if (json.has("wrapped"))
			{
				type = DummyRebornIngredient.WRAPPED_RECIPE_TYPE;
			}

			if (json.has("type"))
			{
				type = new ResourceLocation(json.get("type").getAsString());
			}

			if (!type.equals(DummyRebornIngredient.STACK_RECIPE_TYPE) && !type.equals(DummyRebornIngredient.TAG_RECIPE_TYPE))
			{
				return new DummyRebornIngredient(type, json);
			}
		}

		return super.parseIngredientItem(o, key);
	}

	@Override
	public JsonElement serializeIngredientStack(IngredientStackJS in)
	{
		JsonObject o = in.ingredient.toJson().getAsJsonObject();
		o.addProperty("count", in.getCount());
		return o;
	}
}
