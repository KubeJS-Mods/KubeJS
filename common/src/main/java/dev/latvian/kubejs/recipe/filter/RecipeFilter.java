package dev.latvian.kubejs.recipe.filter;

import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.UtilsJS;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface RecipeFilter extends Predicate<RecipeJS> {
	RecipeFilter ALWAYS_TRUE = r -> true;
	RecipeFilter ALWAYS_FALSE = r -> false;

	boolean test(RecipeJS r);

	static RecipeFilter of(@Nullable Object o) {
		if (o == null || o == ALWAYS_TRUE) {
			return ALWAYS_TRUE;
		} else if (o == ALWAYS_FALSE) {
			return ALWAYS_FALSE;
		}

		ListJS list = ListJS.orSelf(o);

		if (list.isEmpty()) {
			return ALWAYS_TRUE;
		} else if (list.size() > 1) {
			OrFilter predicate = new OrFilter();

			for (Object o1 : list) {
				RecipeFilter p = of(o1);

				if (p == ALWAYS_TRUE) {
					return ALWAYS_TRUE;
				} else if (p != ALWAYS_FALSE) {
					predicate.list.add(p);
				}
			}

			return predicate.list.isEmpty() ? ALWAYS_FALSE : predicate.list.size() == 1 ? predicate.list.get(0) : predicate;
		}

		MapJS map = MapJS.of(list.get(0));

		if (map == null || map.isEmpty()) {
			return ALWAYS_TRUE;
		}

		boolean exact = Boolean.TRUE.equals(map.get("exact"));

		AndFilter predicate = new AndFilter();

		if (map.get("or") != null) {
			predicate.list.add(of(map.get("or")));
		}

		if (map.get("not") != null) {
			predicate.list.add(new NotFilter(of(map.get("not"))));
		}

		try {
			if (map.get("id") != null) {
				String s = map.get("id").toString();
				Pattern pattern = UtilsJS.parseRegex(s);
				predicate.list.add(pattern == null ? new IDFilter(UtilsJS.getMCID(s)) : new RegexIDFilter(pattern));
			}

			if (map.get("type") != null) {
				predicate.list.add(new TypeFilter(UtilsJS.getID(map.get("type").toString())));
			}

			if (map.get("group") != null) {
				predicate.list.add(new GroupFilter(map.get("group").toString()));
			}

			if (map.get("mod") != null) {
				predicate.list.add(new ModFilter(map.get("mod").toString()));
			}

			if (map.get("input") != null) {
				predicate.list.add(new InputFilter(IngredientJS.of(map.get("input")), exact));
			}

			if (map.get("output") != null) {
				predicate.list.add(new OutputFilter(IngredientJS.of(map.get("output")), exact));
			}

			return predicate.list.isEmpty() ? ALWAYS_TRUE : predicate.list.size() == 1 ? predicate.list.get(0) : predicate;
		} catch (RecipeExceptionJS ex) {
			if (ex.error) {
				ScriptType.SERVER.console.error(ex.getMessage());
			} else {
				ScriptType.SERVER.console.warn(ex.getMessage());
			}

			return ALWAYS_FALSE;
		}
	}
}