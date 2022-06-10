package dev.latvian.mods.kubejs.level.gen.filter.mob;

import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

@FunctionalInterface
public interface MobFilter extends BiPredicate<MobCategory, MobSpawnSettings.SpawnerData> {
	MobFilter ALWAYS_TRUE = (cat, data) -> true;
	MobFilter ALWAYS_FALSE = (cat, data) -> false;

	boolean test(MobCategory cat, MobSpawnSettings.SpawnerData data);

	static MobFilter of(@Nullable Object o) {
		if (o == null || o == ALWAYS_TRUE) {
			return ALWAYS_TRUE;
		} else if (o == ALWAYS_FALSE) {
			return ALWAYS_FALSE;
		}

		if (o instanceof String s) {
			return idFilter(s);
		} else if (o instanceof NativeRegExp || o instanceof Pattern) {
			return idFilter(o.toString());
		}

		var list = ListJS.orSelf(o);
		if (list.isEmpty()) {
			return ALWAYS_TRUE;
		} else if (list.size() > 1) {
			var filters = new ArrayList<MobFilter>();

			for (var o1 : list) {
				var filter = of(o1);

				if (filter == ALWAYS_TRUE) {
					return ALWAYS_TRUE;
				} else if (filter != ALWAYS_FALSE) {
					filters.add(filter);
				}
			}

			return filters.isEmpty() ? ALWAYS_FALSE : filters.size() == 1 ? filters.get(0) : new OrFilter(filters);
		}

		var map = MapJS.of(list.get(0));
		if (map == null || map.isEmpty()) {
			return ALWAYS_TRUE;
		}

		var filters = new ArrayList<MobFilter>();

		if (map.get("or") != null) {
			filters.add(of(map.get("or")));
		}

		if (map.get("not") != null) {
			filters.add(new NotFilter(of(map.get("not"))));
		}

		try {
			if (map.get("id") != null) {
				filters.add(idFilter(map.get("id").toString()));
			}

			if (map.get("type") != null) {
				filters.add(idFilter(map.get("type").toString()));
			}

			if (map.get("category") != null) {
				filters.add(new CategoryFilter(UtilsJS.mobCategoryByName(map.get("category").toString())));
			}

			// TODO: Add other mob filters
		} catch (Exception ex) {
			ConsoleJS.STARTUP.error("Error trying to create MobFilter: " + ex.getMessage());
			return ALWAYS_FALSE;
		}

		return filters.isEmpty() ? ALWAYS_TRUE : filters.size() == 1 ? filters.get(0) : new AndFilter(filters);
	}

	static MobFilter idFilter(String s) {
		var pattern = UtilsJS.parseRegex(s);
		if (pattern != null) {
			return new RegexIDFilter(pattern);
		}

		return s.charAt(0) == '#' ? new CategoryFilter(UtilsJS.mobCategoryByName(s.substring(1))) : new IDFilter(UtilsJS.getMCID(s));
	}

}
