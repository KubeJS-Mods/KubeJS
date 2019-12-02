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
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface IngredientJS extends JsonSerializable
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
		else if (o instanceof String)
		{
			if (o.toString().startsWith("#"))
			{
				String[] s = o.toString().substring(1).split(" ", 2);
				return new TagIngredientJS(new ResourceLocation(s[0])).count(s.length == 2 ? UtilsJS.parseInt(s[1], 1) : 1);
			}
			else if (o.toString().startsWith("mod:"))
			{
				return new ModIngredientJS(o.toString().substring(4));
			}

			return ItemStackJS.of(KubeJS.appendModId(o.toString()));
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
			if (map.containsKey("tag"))
			{
				TagIngredientJS ingredient = new TagIngredientJS(new ResourceLocation(map.get("tag").toString()));

				if (map.containsKey("count"))
				{
					return ingredient.count(UtilsJS.parseInt(map.get("count"), 1));
				}

				return ingredient;
			}
			else if (map.containsKey("mod"))
			{
				return new ModIngredientJS(map.get("mod").toString());
			}
		}

		return ItemStackJS.of(o);
	}

	static IngredientJS fromRecipeJson(JsonElement json)
	{
		if (json.isJsonArray())
		{
			MatchAnyIngredientJS any = new MatchAnyIngredientJS();

			for (JsonElement e : json.getAsJsonArray())
			{
				any.ingredients.add(fromRecipeJson(e));
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

			if (o.has("tag"))
			{
				return new TagIngredientJS(new ResourceLocation(o.get("tag").getAsString()));
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

	default IngredientJS count(int count)
	{
		return new IngredientStackJS(this, count);
	}

	default int getCount()
	{
		return 1;
	}

	@Override
	default JsonElement getJson()
	{
		JsonArray array = new JsonArray();

		for (ItemStackJS stackJS : getStacks())
		{
			array.add(stackJS.getJson());
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