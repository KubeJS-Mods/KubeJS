package dev.latvian.kubejs.integration.jei;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.util.FieldJS;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.UtilsJS;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.runtime.IJeiRuntime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author LatvianModder
 */
public class HideCustomJEIEventJS extends EventJS
{
	private final IJeiRuntime runtime;
	private final HashMap<String, HideJEIEventJS> events;

	public HideCustomJEIEventJS(IJeiRuntime r)
	{
		runtime = r;
		events = new HashMap<>();
	}

	@SuppressWarnings("all")
	public HideJEIEventJS get(String s)
	{
		return events.computeIfAbsent(s, type -> {
			try
			{
				int d = type.lastIndexOf('.');
				String cname = type.substring(0, d);
				String fname = type.substring(d + 1);
				FieldJS<IIngredientType> field = UtilsJS.getField(cname, fname);
				IIngredientType t = field.staticGet().orElse(null);

				if (t == null)
				{
					throw new NullPointerException();
				}

				return new HideJEIEventJS(runtime, t, o -> {
					List list = new ArrayList();

					for (Object o1 : ListJS.orSelf(o))
					{
						list.add(UtilsJS.cast(o1));
					}

					return list;
				}, o -> true);
			}
			catch (Exception ex)
			{
				throw new IllegalArgumentException("Unknown or inaccessible type!", ex);
			}
		});
	}

	@Override
	protected void afterPosted(boolean result)
	{
		for (HideJEIEventJS<?> eventJS : events.values())
		{
			eventJS.afterPosted(result);
		}
	}
}