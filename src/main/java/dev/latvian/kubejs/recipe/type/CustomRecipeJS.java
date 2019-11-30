package dev.latvian.kubejs.recipe.type;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJS;
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
			CustomRecipeJS recipe = new CustomRecipeJS();
			recipe.typeId = new ResourceLocation(json.get("type").getAsString());
			recipe.data = json;
			return recipe;
		}
	}

	public static final CustomType TYPE = new CustomType(new ResourceLocation(KubeJS.MOD_ID, "custom"));

	public ResourceLocation typeId;
	public JsonObject data;

	@Override
	public RecipeTypeJS getType()
	{
		return TYPE;
	}

	@Override
	public JsonObject toJson()
	{
		JsonObject json = JsonUtilsJS.copy(data).getAsJsonObject();
		json.addProperty("type", typeId.toString());

		if (!group.isEmpty())
		{
			json.addProperty("group", group);
		}

		return json;
	}
}