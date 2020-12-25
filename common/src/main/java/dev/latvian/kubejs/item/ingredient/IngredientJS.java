package dev.latvian.kubejs.item.ingredient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.item.BoundItemStackJS;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.kubejs.util.JsonSerializable;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.util.WrappedJS;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import me.shedaniel.architectury.ExpectPlatform;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface IngredientJS extends JsonSerializable, WrappedJS
{
	static IngredientJS of(@Nullable Object o)
	{
		if (o instanceof Wrapper)
		{
			o = ((Wrapper) o).unwrap();
		}

		if (o == null)
		{
			return EmptyItemStackJS.INSTANCE;
		}
		else if (o instanceof IngredientJS)
		{
			return (IngredientJS) o;
		}
		else if (o instanceof Pattern || o instanceof NativeRegExp)
		{
			Pattern reg = UtilsJS.parseRegex(o);

			if (reg != null)
			{
				return new RegexIngredientJS(reg);
			}

			return EmptyItemStackJS.INSTANCE;
		}
		else if (o instanceof JsonElement)
		{
			return ingredientFromRecipeJson((JsonElement) o);
		}
		else if (o instanceof CharSequence)
		{
			String s = o.toString();

			if (s.equals("*"))
			{
				return MatchAllIngredientJS.INSTANCE;
			}
			else if (s.isEmpty() || s.equals("-") || s.equals("air") || s.equals("minecraft:air"))
			{
				return EmptyItemStackJS.INSTANCE;
			}
			else if (s.startsWith("#"))
			{
				return TagIngredientJS.createTag(s.substring(1));
			}
			else if (s.startsWith("@"))
			{
				return new ModIngredientJS(s.substring(1));
			}
			else if (s.startsWith("%"))
			{
				CreativeModeTab group = ItemStackJS.findGroup(s.substring(1));

				if (group == null)
				{
					return EmptyItemStackJS.INSTANCE;
				}
				else if (group == CreativeModeTab.TAB_SEARCH)
				{
					return MatchAllIngredientJS.INSTANCE;
				}

				return new GroupIngredientJS(group);
			}

			Pattern reg = UtilsJS.regex(s);

			if (reg != null)
			{
				return new RegexIngredientJS(reg);
			}

			return ItemStackJS.of(KubeJS.appendModId(s));
		}

		List<Object> list = ListJS.of(o);

		if (list != null)
		{
			MatchAnyIngredientJS l = new MatchAnyIngredientJS();

			for (Object o1 : list)
			{
				IngredientJS ingredient = of(o1);

				if (ingredient != EmptyItemStackJS.INSTANCE)
				{
					l.ingredients.add(ingredient);
				}
			}

			return l.ingredients.isEmpty() ? EmptyItemStackJS.INSTANCE : l;
		}

		MapJS map = MapJS.of(o);

		if (map != null)
		{
			IngredientJS in = EmptyItemStackJS.INSTANCE;
			boolean val = map.containsKey("value");

			if (map.containsKey("type"))
			{
				JsonObject json = map.toJson();

				try
				{
					Ingredient ingredient = getCustomIngredient(json);
					return new CustomIngredient(ingredient, json);
				}
				catch (Exception ex)
				{
					throw new RecipeExceptionJS("Failed to parse custom ingredient (" + json.get("type") + ") from " + json + ": " + ex);
				}
			}
			else if (val || map.containsKey("ingredient"))
			{
				in = of(val ? map.get("value") : map.get("ingredient"));
			}
			else if (map.containsKey("tag"))
			{
				in = TagIngredientJS.createTag(map.get("tag").toString());
			}
			else if (map.containsKey("item"))
			{
				in = ItemStackJS.of(map);
			}

			if (map.containsKey("count"))
			{
				in = in.count(UtilsJS.parseInt(map.get("count"), 1));
			}
			else if (map.containsKey("amount"))
			{
				in = in.count(UtilsJS.parseInt(map.get("amount"), 1));

				if (in instanceof IngredientStackJS)
				{
					((IngredientStackJS) in).countKey = "amount";
				}
			}

			if (val && in instanceof IngredientStackJS)
			{
				((IngredientStackJS) in).ingredientKey = "value";
			}

			return in;
		}

		return ItemStackJS.of(o);
	}

	@ExpectPlatform
	static Ingredient getCustomIngredient(JsonObject object)
	{
		throw new AssertionError();
	}

	static IngredientJS ingredientFromRecipeJson(JsonElement json)
	{
		if (json.isJsonArray())
		{
			MatchAnyIngredientJS any = new MatchAnyIngredientJS();

			for (JsonElement e : json.getAsJsonArray())
			{
				any.ingredients.add(ingredientFromRecipeJson(e));
			}

			return any;
		}
		else if (json.isJsonPrimitive())
		{
			return of(json.getAsString());
		}
		else if (json.isJsonObject())
		{
			JsonObject o = json.getAsJsonObject();
			IngredientJS in = EmptyItemStackJS.INSTANCE;
			boolean val = o.has("value");

			if (o.has("type"))
			{
				try
				{
					Ingredient ingredient = getCustomIngredient(o);
					return new CustomIngredient(ingredient, o);
				}
				catch (Exception ex)
				{
					throw new RecipeExceptionJS("Failed to parse custom ingredient (" + o.get("type") + ") from " + o + ": " + ex);
				}
			}
			else if (val || o.has("ingredient"))
			{
				in = ingredientFromRecipeJson(val ? o.get("value") : o.get("ingredient"));
			}
			else if (o.has("tag"))
			{
				in = TagIngredientJS.createTag(o.get("tag").getAsString());
			}
			else if (o.has("item"))
			{
				in = ItemStackJS.of(o.get("item").getAsString());
			}

			if (o.has("count"))
			{
				in = in.count(o.get("count").getAsInt());
			}
			else if (o.has("amount"))
			{
				in = in.count(o.get("amount").getAsInt());

				if (in instanceof IngredientStackJS)
				{
					((IngredientStackJS) in).countKey = "amount";
				}
			}

			if (val && in instanceof IngredientStackJS)
			{
				((IngredientStackJS) in).ingredientKey = "value";
			}

			return in;
		}

		return EmptyItemStackJS.INSTANCE;
	}

	boolean test(ItemStackJS stack);

	default boolean testVanilla(ItemStack stack)
	{
		return test(new BoundItemStackJS(stack));
	}

	default Predicate<ItemStack> getVanillaPredicate()
	{
		return new VanillaPredicate(this);
	}

	default boolean isEmpty()
	{
		return getFirst().isEmpty();
	}

	default boolean isInvalidRecipeIngredient()
	{
		return false;
	}

	default Set<ItemStackJS> getStacks()
	{
		Set<ItemStackJS> set = new LinkedHashSet<>();

		for (ItemStackJS stack : ItemStackJS.getList())
		{
			if (test(stack))
			{
				set.add(stack.getCopy());
			}
		}

		return set;
	}

	default IngredientJS filter(IngredientJS filter)
	{
		return new FilteredIngredientJS(this, filter);
	}

	default IngredientJS not()
	{
		return new NotIngredientJS(this);
	}

	default ItemStackJS getFirst()
	{
		for (ItemStackJS stack : getStacks())
		{
			if (!stack.isEmpty())
			{
				return stack.count(getCount());
			}
		}

		return EmptyItemStackJS.INSTANCE;
	}

	default IngredientJS withCount(int count)
	{
		if (count <= 0)
		{
			return EmptyItemStackJS.INSTANCE;
		}
		else if (count == 1 || count == getCount())
		{
			return this;
		}

		return new IngredientStackJS(this, count);
	}

	@Deprecated
	default IngredientJS count(int count)
	{
		return withCount(count);
	}

	default IngredientJS getCopy()
	{
		return this;
	}

	default int getCount()
	{
		return 1;
	}

	@Override
	default JsonElement toJson()
	{
		Set<ItemStackJS> set = getStacks();

		if (set.size() == 1)
		{
			return set.iterator().next().toJson();
		}

		JsonArray array = new JsonArray();

		for (ItemStackJS stackJS : set)
		{
			array.add(stackJS.toJson());
		}

		return array;
	}

	default boolean anyStackMatches(IngredientJS ingredient)
	{
		for (ItemStackJS stack : getStacks())
		{
			if (ingredient.test(stack))
			{
				return true;
			}
		}

		return false;
	}

	default IngredientStackJS asIngredientStack()
	{
		return new IngredientStackJS(count(1), getCount());
	}
}