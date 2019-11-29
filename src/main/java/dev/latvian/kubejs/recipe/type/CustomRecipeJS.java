package dev.latvian.kubejs.recipe.type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.recipe.RecipeDeserializerJS;
import dev.latvian.kubejs.recipe.RecipeProviderJS;
import dev.latvian.kubejs.util.JsonUtilsJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author LatvianModder
 */
public class CustomRecipeJS extends RecipeJS
{
	public static final RecipeProviderJS PROVIDER = args -> {
		if (args.length == 2)
		{
			IRecipeSerializer serializer = ForgeRegistries.RECIPE_SERIALIZERS.getValue(UtilsJS.getID(args[0]));
			JsonElement data = JsonUtilsJS.of(args[1]);

			if (serializer != null && data.isJsonObject())
			{
				CustomRecipeJS recipe = new CustomRecipeJS();
				recipe.type = serializer;
				recipe.data = data.getAsJsonObject();
				return recipe;
			}
		}

		return null;
	};

	public static final RecipeDeserializerJS DESERIALIZER = json -> {
		if (json.has("type"))
		{
			IRecipeSerializer serializer = ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation(json.get("type").getAsString()));

			if (serializer != null)
			{
				CustomRecipeJS recipe = new CustomRecipeJS();
				recipe.type = serializer;
				recipe.data = json;
				return recipe;
			}
		}

		return null;
	};

	public IRecipeSerializer type;
	public JsonObject data;

	@Override
	public IRecipeSerializer getSerializer()
	{
		return type;
	}

	@Override
	public JsonObject toJson()
	{
		JsonObject json = JsonUtilsJS.copy(data).getAsJsonObject();
		json.addProperty("type", type.getRegistryName().toString());

		if (!group.isEmpty())
		{
			json.addProperty("group", group);
		}

		return json;
	}
}