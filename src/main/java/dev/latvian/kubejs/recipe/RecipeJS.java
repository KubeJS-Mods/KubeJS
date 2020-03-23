package dev.latvian.kubejs.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public abstract class RecipeJS
{
	public ResourceLocation id;
	public RecipeTypeJS type;
	public JsonObject json = null;
	public IRecipe<?> originalRecipe = null;
	public final List<IngredientJS> inputItems = new ArrayList<>(1);
	public final List<ItemStackJS> outputItems = new ArrayList<>(1);

	public abstract void create(ListJS args);

	public abstract void deserialize();

	public abstract void serialize();

	public final void save()
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

	public final boolean hasInput(IngredientJS ingredient, boolean exact)
	{
		for (IngredientJS in : inputItems)
		{
			if (exact ? in.equals(ingredient) : in.anyStackMatches(ingredient))
			{
				return true;
			}
		}

		return false;
	}

	public final boolean replaceInput(IngredientJS i, IngredientJS with, boolean exact)
	{
		boolean changed = false;

		for (int j = 0; j < inputItems.size(); j++)
		{
			if (exact ? inputItems.get(j).equals(i) : inputItems.get(j).anyStackMatches(i))
			{
				inputItems.set(j, IngredientJS.of(with));
				changed = true;
				save();
			}
		}

		return changed;
	}

	public final boolean hasOutput(IngredientJS ingredient, boolean exact)
	{
		for (ItemStackJS out : outputItems)
		{
			if (exact ? ingredient.equals(out) : ingredient.test(out))
			{
				return true;
			}
		}

		return false;
	}

	public final boolean replaceOutput(IngredientJS i, ItemStackJS with, boolean exact)
	{
		boolean changed = false;

		for (int j = 0; j < outputItems.size(); j++)
		{
			if (exact ? i.equals(outputItems.get(j)) : i.test(outputItems.get(j)))
			{
				outputItems.set(j, with.getCopy().count(outputItems.get(j).getCount())).chance(outputItems.get(j).getChance());
				changed = true;
				save();
			}
		}

		return changed;
	}

	public String getGroup()
	{
		JsonElement e = json.get("group");
		return e instanceof JsonPrimitive ? e.getAsString() : "";
	}

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

	public String getId()
	{
		return id.toString();
	}

	public String getMod()
	{
		return id.getNamespace();
	}

	public String getPath()
	{
		return id.getPath();
	}

	public String getType()
	{
		return type.toString();
	}
}