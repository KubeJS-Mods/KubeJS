package dev.latvian.kubejs.recipe.type;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.recipe.RecipeErrorJS;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.util.JsonUtilsJS;
import net.minecraft.util.ResourceLocation;

/**
 * @author LatvianModder
 */
public class CustomRecipeJS extends RecipeJS
{
	public static class CustomType extends RecipeTypeJS
	{
		public CustomType(ResourceLocation id)
		{
			super(id);
		}

		@Override
		public RecipeJS create(Object[] args)
		{
			return new RecipeErrorJS("Can't create custom recipe!");
		}

		@Override
		public RecipeJS create(JsonObject json)
		{
			if (serializer == null)
			{
				return new RecipeErrorJS("Recipe type '" + id + "' is not registered!");
			}

			try
			{
				CustomRecipeJS recipe = new CustomRecipeJS(this);
				recipe.data = json;
				serializer.read(new ResourceLocation("dummy"), recipe.data);
				return recipe;
			}
			catch (Exception ex)
			{
				return new RecipeErrorJS(ex.toString());
			}
		}
	}

	public final CustomType type;
	public JsonObject data;

	public CustomRecipeJS(CustomType t)
	{
		type = t;
	}

	@Override
	public RecipeTypeJS getType()
	{
		return type;
	}

	@Override
	public JsonObject toJson()
	{
		JsonObject json = JsonUtilsJS.copy(data).getAsJsonObject();
		json.addProperty("type", type.id.toString());

		if (!group.isEmpty())
		{
			json.addProperty("group", group);
		}

		return json;
	}
}