package dev.latvian.kubejs.item.ingredient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.item.BoundItemStackJS;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.util.JsonSerializable;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.util.WrappedJS;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;

import javax.annotation.Nullable;
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
		if (o == null)
		{
			return EmptyItemStackJS.INSTANCE;
		}
		else if (o instanceof IngredientJS)
		{
			return (IngredientJS) o;
		}
		else if (o instanceof Pattern)
		{
			return new RegexIngredientJS((Pattern) o);
		}
		else if (o instanceof CharSequence)
		{
			String s = o.toString();

			if (s.equals("*"))
			{
				return MatchAllIngredientJS.INSTANCE;
			}
			else if (s.startsWith("#"))
			{
				return new TagIngredientJS(s.substring(1));
			}
			else if (s.startsWith("@"))
			{
				return new ModIngredientJS(s.substring(1));
			}
			else if (s.startsWith("%"))
			{
				ItemGroup group = ItemStackJS.findGroup(s.substring(1));

				if (group == null)
				{
					return EmptyItemStackJS.INSTANCE;
				}
				else if (group == ItemGroup.SEARCH)
				{
					return MatchAllIngredientJS.INSTANCE;
				}

				return new GroupIngredientJS(group);
			}

			Pattern reg = UtilsJS.regex(s, true);

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
			if (map.containsKey("ingredient"))
			{
				IngredientJS in = of(map.get("ingredient"));

				if (in.isEmpty())
				{
					return in;
				}

				IngredientStackJS stack = new IngredientStackJS(in, 1);

				if (map.containsKey("count"))
				{
					stack.count(UtilsJS.parseInt(map.get("count"), 1));
				}
				else if (map.containsKey("amount"))
				{
					stack.count(UtilsJS.parseInt(map.get("amount"), 1));
					stack.countKey = "amount";
				}

				return stack;
			}
			else if (map.containsKey("type"))
			{
				try
				{
					JsonObject json = map.toJson();
					Ingredient ingredient = CraftingHelper.getIngredient(json);
					return new CustomIngredient(ingredient, json);
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
					return stack -> false;
				}
			}
			else if (map.containsKey("tag"))
			{
				return new TagIngredientJS(map.get("tag").toString());
			}
		}

		return ItemStackJS.of(o);
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

			if (o.has("ingredient"))
			{
				IngredientJS in = ingredientFromRecipeJson(o.get("ingredient"));

				if (o.has("count"))
				{
					return in.count(o.get("count").getAsInt());
				}
				else if (o.has("amount"))
				{
					in = in.count(o.get("amount").getAsInt());

					if (in instanceof IngredientStackJS)
					{
						((IngredientStackJS) in).countKey = "amount";
					}
				}

				return in;
			}
			else if (o.has("type"))
			{
				try
				{
					Ingredient ingredient = CraftingHelper.getIngredient(o);
					return new CustomIngredient(ingredient, o);
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
					return stack -> false;
				}
			}
			else if (o.has("tag"))
			{
				return new TagIngredientJS(o.get("tag").getAsString());
			}
			else if (o.has("item"))
			{
				ItemStackJS stack = ItemStackJS.of(o.get("item").getAsString());

				if (o.has("count"))
				{
					stack.setCount(o.get("count").getAsInt());
				}

				return stack;
			}
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

	default IngredientJS count(int count)
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
}