package dev.latvian.mods.kubejs.level.gen.filter.biome;

import dev.architectury.registry.level.biome.BiomeModifications;
import dev.latvian.mods.kubejs.level.LevelPlatformHelper;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import net.minecraft.world.level.biome.Biome.BiomeCategory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@FunctionalInterface
public interface BiomeFilter extends Predicate<BiomeModifications.BiomeContext> {
	BiomeFilter ALWAYS_TRUE = ctx -> true;
	BiomeFilter ALWAYS_FALSE = ctx -> false;

	@Override
	boolean test(BiomeModifications.BiomeContext ctx);

	static BiomeFilter of(@Nullable Object o) {
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
			List<BiomeFilter> filters = new ArrayList<>();
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

		List<BiomeFilter> filters = new ArrayList<>();

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
				filters.add(new CategoryFilter(BiomeCategory.byName(map.get("category").toString())));
			}

			// allow platform-specific hooks
			var additional = LevelPlatformHelper.get().ofMapAdditional(map);
			if (additional != null) {
				filters.add(additional);
			}

			// TODO: Add other biome property filters
		} catch (Exception ex) {
			ConsoleJS.STARTUP.error("Error trying to create BiomeFilter: " + ex.getMessage());
			return ALWAYS_FALSE;
		}

		return filters.isEmpty() ? ALWAYS_TRUE : filters.size() == 1 ? filters.get(0) : new AndFilter(filters);
	}

	static BiomeFilter idFilter(String s) {
		var pattern = UtilsJS.parseRegex(s);
		if (pattern != null) {
			return new RegexIDFilter(pattern);
		}
		if (s.charAt(0) == '^') {
			return new CategoryFilter(BiomeCategory.byName(s.substring(1)));
		}

		// allow platform-specific hooks
		var additional = LevelPlatformHelper.get().ofStringAdditional(s);
		if (additional != null) {
			return additional;
		}

		return new IDFilter(UtilsJS.getMCID(s));
	}
}
