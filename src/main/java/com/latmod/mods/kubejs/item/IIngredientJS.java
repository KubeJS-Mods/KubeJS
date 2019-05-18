package com.latmod.mods.kubejs.item;

import com.latmod.mods.kubejs.KubeJS;
import com.latmod.mods.kubejs.util.UtilsJS;
import jdk.nashorn.api.scripting.JSObject;
import net.minecraftforge.common.crafting.JsonContext;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public interface IIngredientJS extends Predicate<ItemStackJS>
{
	JsonContext CONTEXT = new JsonContext(KubeJS.MOD_ID);

	static IIngredientJS get(@Nullable Object object)
	{
		if (object instanceof String)
		{
			if (object.toString().startsWith("ore:"))
			{
				return new OreDictionaryIngredientJS(object.toString().substring(4));
			}

			return UtilsJS.INSTANCE.item(CONTEXT.appendModId(object.toString()));
		}
		else if (object instanceof JSObject)
		{
			JSObject js = (JSObject) object;

			if (js.isArray())
			{
				IngredientListJS list = new IngredientListJS();

				for (String key : js.keySet())
				{
					IIngredientJS ingredient = get(js.getMember(key));

					if (ingredient != ItemStackJS.EMPTY)
					{
						list.ingredients.add(ingredient);
					}
				}

				return list.ingredients.isEmpty() ? ItemStackJS.EMPTY : list;
			}
		}

		return UtilsJS.INSTANCE.item(object);
	}
}