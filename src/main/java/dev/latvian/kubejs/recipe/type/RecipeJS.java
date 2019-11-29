package dev.latvian.kubejs.recipe.type;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.script.data.VirtualKubeJSDataPack;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public abstract class RecipeJS
{
	public static final RecipeJS ERROR = new RecipeJS()
	{
		@Nullable
		@Override
		public IRecipeSerializer getSerializer()
		{
			return null;
		}

		@Override
		public JsonObject toJson()
		{
			JsonObject json = new JsonObject();
			json.addProperty("type", "kubejs:error");
			return json;
		}
	};

	public ResourceLocation id;
	public String group;

	@Nullable
	public abstract IRecipeSerializer getSerializer();

	public abstract JsonObject toJson();

	public void addToDataPack(VirtualKubeJSDataPack pack)
	{
		pack.addData(new ResourceLocation(id.getNamespace(), "recipes/" + id.getPath() + ".json"), toJson().toString());
	}

	public RecipeJS id(@P("id") Object _id)
	{
		id = UtilsJS.getID(_id);
		return this;
	}

	public RecipeJS group(@P("group") String g)
	{
		group = g;
		return this;
	}

	protected JsonObject create()
	{
		JsonObject json = new JsonObject();
		json.addProperty("type", getSerializer().getRegistryName().toString());

		if (!group.isEmpty())
		{
			json.addProperty("group", group);
		}

		return json;
	}

	public boolean hasInput(IngredientJS ingredient)
	{
		return false;
	}

	public boolean hasOutput(IngredientJS ingredient)
	{
		return false;
	}

	@Override
	public String toString()
	{
		IRecipeSerializer s = getSerializer();
		return s == null ? id.toString() : (id + "[" + s.getRegistryName() + "]");
	}
}