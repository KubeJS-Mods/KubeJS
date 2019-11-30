package dev.latvian.kubejs.recipe;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.recipe.type.RecipeJS;
import net.minecraft.util.ResourceLocation;

/**
 * @author LatvianModder
 */
public class RecipeErrorJS extends RecipeJS
{
	public static final RecipeTypeJS TYPE = new RecipeTypeJS(new ResourceLocation(KubeJS.MOD_ID, "error"))
	{
		@Override
		public RecipeJS create(Object[] args)
		{
			return new RecipeErrorJS("Unknown");
		}

		@Override
		public RecipeJS create(JsonObject json)
		{
			return new RecipeErrorJS("Unknown");
		}
	};

	public final String message;

	public RecipeErrorJS(String m)
	{
		message = m;
	}

	@Override
	public RecipeTypeJS getType()
	{
		return TYPE;
	}

	@Override
	public JsonObject toJson()
	{
		return new JsonObject();
	}
}