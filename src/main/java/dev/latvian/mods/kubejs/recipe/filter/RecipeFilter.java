package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.core.RecipeLikeKJS;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.RegExpJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

@FunctionalInterface
public interface RecipeFilter {
	boolean test(Context cx, RecipeLikeKJS r);

	static RecipeFilter of(Context cx, @Nullable Object o) {
		if (o == null || o == ConstantFilter.TRUE) {
			return ConstantFilter.TRUE;
		} else if (o == ConstantFilter.FALSE) {
			return ConstantFilter.FALSE;
		} else if (o instanceof CharSequence || o instanceof NativeRegExp || o instanceof Pattern) {
			String s = o.toString();

			if (s.equals("*")) {
				return ConstantFilter.TRUE;
			} else if (s.equals("-")) {
				return ConstantFilter.FALSE;
			} else {
				var r = RegExpJS.wrap(s);
				return r == null ? new IDFilter(ID.mc(s)) : RegexIDFilter.of(r);
			}
		}

		var list = ListJS.orSelf(o);

		if (list.isEmpty()) {
			return ConstantFilter.FALSE;
		} else if (list.size() > 1) {
			var predicate = new OrFilter();

			for (var o1 : list) {
				var p = of(cx, o1);

				if (p == ConstantFilter.TRUE) {
					return ConstantFilter.TRUE;
				} else if (p != ConstantFilter.FALSE) {
					predicate.list.add(p);
				}
			}

			return predicate.list.isEmpty() ? ConstantFilter.FALSE : predicate.list.size() == 1 ? predicate.list.getFirst() : predicate;
		}

		var map = MapJS.of(list.getFirst());

		if (map == null || map.isEmpty()) {
			return ConstantFilter.TRUE;
		}

		var predicate = new AndFilter();

		if (map.get("or") != null) {
			predicate.list.add(of(cx, map.get("or")));
		}

		if (map.get("not") != null) {
			predicate.list.add(new NotFilter(of(cx, map.get("not"))));
		}

		try {
			var id = map.get("id");

			if (id != null) {
				var pattern = RegExpJS.wrap(id);
				predicate.list.add(pattern == null ? new IDFilter(ID.mc(id)) : RegexIDFilter.of(pattern));
			}

			var type = map.get("type");

			if (type != null) {
				predicate.list.add(new TypeFilter(ID.mc(type)));
			}

			var group = map.get("group");

			if (group != null) {
				predicate.list.add(new GroupFilter(group.toString()));
			}

			var mod = map.get("mod");

			if (mod != null) {
				predicate.list.add(new ModFilter(mod.toString()));
			}

			var input = map.get("input");

			if (input != null) {
				predicate.list.add(new InputFilter(ReplacementMatch.of(((KubeJSContext) cx).getRegistries(), input)));
			}

			var output = map.get("output");

			if (output != null) {
				predicate.list.add(new OutputFilter(ReplacementMatch.of(((KubeJSContext) cx).getRegistries(), output)));
			}

			NeoForge.EVENT_BUS.post(new RecipeFilterParseEvent(cx, predicate.list, map));

			return predicate.list.isEmpty() ? ConstantFilter.TRUE : predicate.list.size() == 1 ? predicate.list.getFirst() : predicate;
		} catch (RecipeExceptionJS rex) {
			if (rex.error) {
				ConsoleJS.getCurrent(cx).error(rex.getMessage());
			} else {
				ConsoleJS.getCurrent(cx).warn(rex.getMessage());
			}

			return ConstantFilter.FALSE;
		}
	}
}