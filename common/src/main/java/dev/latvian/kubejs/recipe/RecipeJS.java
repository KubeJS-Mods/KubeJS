package dev.latvian.kubejs.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.IngredientStackJS;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author LatvianModder
 */
public abstract class RecipeJS
{
	public static RecipeJS currentRecipe = null;

	public ResourceLocation id;
	public RecipeTypeJS type;
	public JsonObject json = null;
	public Recipe<?> originalRecipe = null;
	public final List<IngredientJS> inputItems = new ArrayList<>(1);
	public final List<ItemStackJS> outputItems = new ArrayList<>(1);

	public abstract void create(ListJS args);

	public abstract void deserialize();

	public abstract void serialize();

	public final void deserializeJson()
	{
		currentRecipe = this;
		deserialize();
		currentRecipe = null;
	}

	public final void serializeJson()
	{
		currentRecipe = this;
		json.addProperty("type", type.getId());
		serialize();
		currentRecipe = null;
	}

	public final void save()
	{
		originalRecipe = null;
	}

	public RecipeJS id(@ID String _id)
	{
		id = UtilsJS.getMCID(_id);
		return this;
	}

	public RecipeJS group(@ID String g)
	{
		setGroup(g);
		return this;
	}

	public final boolean hasInput(IngredientJS ingredient, boolean exact)
	{
		return getInputIndex(ingredient, exact) != -1;
	}

	public final int getInputIndex(IngredientJS ingredient, boolean exact)
	{
		for (int i = 0; i < inputItems.size(); i++)
		{
			IngredientJS in = inputItems.get(i);

			if (exact ? in.equals(ingredient) : in.anyStackMatches(ingredient))
			{
				return i;
			}
		}

		return -1;
	}

	public final boolean replaceInput(IngredientJS i, IngredientJS with, boolean exact)
	{
		return replaceInput(i, with, exact, (in, original) -> in.count(original.getCount()));
	}

	public final boolean replaceInput(IngredientJS i, IngredientJS with, boolean exact, BiFunction<IngredientJS, IngredientJS, IngredientJS> func)
	{
		boolean changed = false;

		for (int j = 0; j < inputItems.size(); j++)
		{
			if (exact ? inputItems.get(j).equals(i) : inputItems.get(j).anyStackMatches(i))
			{
				inputItems.set(j, func.apply(with.getCopy(), inputItems.get(j)));
				changed = true;
				save();
			}
		}

		return changed;
	}

	public final boolean hasOutput(IngredientJS ingredient, boolean exact)
	{
		return getOutputIndex(ingredient, exact) != -1;
	}

	public final int getOutputIndex(IngredientJS ingredient, boolean exact)
	{
		for (int i = 0; i < outputItems.size(); i++)
		{
			ItemStackJS out = outputItems.get(i);

			if (exact ? ingredient.equals(out) : ingredient.test(out))
			{
				return i;
			}
		}

		return -1;
	}

	public final boolean replaceOutput(IngredientJS i, ItemStackJS with, boolean exact)
	{
		return replaceOutput(i, with, exact, (out, original) -> out.count(original.getCount()).chance(original.getChance()));
	}

	public final boolean replaceOutput(IngredientJS i, ItemStackJS with, boolean exact, BiFunction<ItemStackJS, ItemStackJS, ItemStackJS> func)
	{
		boolean changed = false;

		for (int j = 0; j < outputItems.size(); j++)
		{
			if (exact ? i.equals(outputItems.get(j)) : i.test(outputItems.get(j)))
			{
				outputItems.set(j, func.apply(with.getCopy(), outputItems.get(j)));
				changed = true;
				save();
			}
		}

		return changed;
	}

	@ID
	public String getGroup()
	{
		JsonElement e = json.get("group");
		return e instanceof JsonPrimitive ? e.getAsString() : "";
	}

	public void setGroup(@ID String g)
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

	@ID
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

	@ID
	public String getType()
	{
		return type.toString();
	}

	public JsonElement serializeIngredientStack(IngredientStackJS in)
	{
		JsonObject json = new JsonObject();
		json.add(in.ingredientKey, in.ingredient.toJson());
		json.addProperty(in.countKey, in.getCount());
		return json;
	}
}