package dev.latvian.kubejs.recipe.type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.IRecipeCollection;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.Collections;

/**
 * @author LatvianModder
 */
public abstract class RecipeJS implements IRecipeCollection
{
	public ResourceLocation id;
	public RecipeTypeJS type;
	public JsonObject json = null;
	public IRecipe<?> originalRecipe = null;
	public boolean remove = false;

	public abstract void create(ListJS args);

	public abstract void deserialize();

	public abstract void serialize();

	public void save()
	{
		originalRecipe = null;
	}

	public RecipeJS id(Object _id)
	{
		id = UtilsJS.getID(_id);
		return this;
	}

	public RecipeJS group(String g)
	{
		setGroup(g);
		return this;
	}

	@Override
	public final void remove()
	{
		if (!remove)
		{
			remove = true;
			String s = "- " + this + ": " + getInput() + " -> " + getOutput();

			if (ServerJS.instance.logRemovedRecipes)
			{
				ScriptType.SERVER.console.logger.info(s);
			}
			else
			{
				ScriptType.SERVER.console.logger.debug(s);
			}
		}
	}

	@Override
	public final int getCount()
	{
		return 1;
	}

	public Collection<IngredientJS> getInput()
	{
		return Collections.emptyList();
	}

	@Override
	public boolean hasInput(Object i)
	{
		IngredientJS ingredient = IngredientJS.of(i);

		for (IngredientJS in : getInput())
		{
			if (in.anyStackMatches(ingredient))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean replaceInput(Object ingredient, Object with)
	{
		return false;
	}

	public Collection<ItemStackJS> getOutput()
	{
		return Collections.emptyList();
	}

	@Override
	public boolean hasOutput(Object i)
	{
		IngredientJS in = IngredientJS.of(i);

		for (ItemStackJS result : getOutput())
		{
			if (in.test(result))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean replaceOutput(Object ingredient, Object with)
	{
		return false;
	}

	public String getGroup()
	{
		JsonElement e = json.get("group");
		return e instanceof JsonPrimitive ? e.getAsString() : "";
	}

	@Override
	public void setGroup(String g)
	{
		if (g.isEmpty())
		{
			json.remove("group");
		}
		else
		{
			json.addProperty("group", g);
		}

		save();
	}

	@Override
	public String toString()
	{
		return id + "[" + type + "]";
	}
}