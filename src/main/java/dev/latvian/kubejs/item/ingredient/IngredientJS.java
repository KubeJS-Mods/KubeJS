package dev.latvian.kubejs.item.ingredient;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.item.BoundItemStackJS;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.util.UtilsJS;
import jdk.nashorn.api.scripting.JSObject;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface IngredientJS
{
	static IngredientJS of(@Nullable Object object)
	{
		if (object instanceof IngredientJS)
		{
			return (IngredientJS) object;
		}
		else if (object instanceof String)
		{
			if (object.toString().startsWith("ore:"))
			{
				String[] s = object.toString().substring(4).split(" ", 2);
				return new OreDictionaryIngredientJS(s[0]).count(s.length == 2 ? UtilsJS.parseInt(s[1], 1) : 1);
			}
			else if (object.toString().startsWith("mod:"))
			{
				return new ModIngredientJS(object.toString().substring(4));
			}

			return ItemStackJS.of(KubeJS.appendModId(object.toString()));
		}
		else if (object instanceof JSObject)
		{
			JSObject js = (JSObject) object;

			if (js.isArray())
			{
				MatchAnyIngredientJS list = new MatchAnyIngredientJS();

				for (String key : js.keySet())
				{
					IngredientJS ingredient = of(js.getMember(key));

					if (ingredient != EmptyItemStackJS.INSTANCE)
					{
						list.ingredients.add(ingredient);
					}
				}

				return list.ingredients.isEmpty() ? EmptyItemStackJS.INSTANCE : list;
			}
			else if (js.hasMember("ore"))
			{
				OreDictionaryIngredientJS ingredient = new OreDictionaryIngredientJS(js.getMember("ore").toString());

				if (js.hasMember("count"))
				{
					return ingredient.count(UtilsJS.parseInt(js.getMember("count"), 1));
				}

				return ingredient;
			}
			else if (js.hasMember("mod"))
			{
				return new ModIngredientJS(js.getMember("mod").toString());
			}
		}

		return ItemStackJS.of(object);
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
}