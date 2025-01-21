package dev.latvian.mods.kubejs.bindings;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.JsonIO;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.regex.Pattern;

public interface StringUtilsWrapper {
	Pattern SNAKE_CASE_SPLIT = Pattern.compile("[:_/]");
	Set<String> ALWAYS_LOWER_CASE = new HashSet<>(Arrays.asList("a", "an", "the", "of", "on", "in", "and", "or", "but", "for"));
	String[] EMPTY_STRING_ARRAY = new String[0];

	@Info("Tries to parse the first parameter as an integer, and returns that. The second parameter is returned if parsing fails")
	static int parseInt(@Nullable Object object, int def) {
		if (object == null) {
			return def;
		} else if (object instanceof Number num) {
			return num.intValue();
		}

		try {
			var s = object.toString();

			if (s.isEmpty()) {
				return def;
			}

			return Integer.parseInt(s);
		} catch (Exception ex) {
			return def;
		}
	}

	static long parseLong(@Nullable Object object, long def) {
		if (object == null) {
			return def;
		} else if (object instanceof Number num) {
			return num.longValue();
		}

		try {
			var s = object.toString();

			if (s.isEmpty()) {
				return def;
			}

			return Long.parseLong(s);
		} catch (Exception ex) {
			return def;
		}
	}

	@Info("Tries to parse the first parameter as a double and returns that. The second parameter is returned if parsing fails")
	static double parseDouble(@Nullable Object object, double def) {
		if (object == null) {
			return def;
		} else if (object instanceof Number num) {
			return num.doubleValue();
		}

		try {
			var s = object.toString();

			if (s.isEmpty()) {
				return def;
			}

			return Double.parseDouble(String.valueOf(object));
		} catch (Exception ex) {
			return def;
		}
	}

	@Info("Returns the provided snake_case_string in camelCase")
	static String snakeCaseToCamelCase(String string) {
		if (string == null || string.isEmpty()) {
			return string;
		}

		var s = SNAKE_CASE_SPLIT.split(string, 0);

		var sb = new StringBuilder();
		var first = true;

		for (var value : s) {
			if (!value.isEmpty()) {
				if (first) {
					first = false;
					sb.append(value);
				} else {
					sb.append(Character.toUpperCase(value.charAt(0)));
					sb.append(value, 1, value.length());
				}
			}
		}

		return sb.toString();
	}

	@Info("Returns the provided snake_case_string in Title Case")
	static String snakeCaseToTitleCase(String string) {
		StringJoiner joiner = new StringJoiner(" ");
		String[] split = string.split("_");
		for (int i = 0; i < split.length; i++) {
			String s = split[i];
			String titleCase = toTitleCase(s, i == 0);
			joiner.add(titleCase);
		}
		return joiner.toString();
	}

	@Info("Capitalises the first letter of the string unless it is \"a\", \"an\", \"the\", \"of\", \"on\", \"in\", \"and\", \"or\", \"but\" or \"for\"")
	static String toTitleCase(String s) {
		return toTitleCase(s, false);
	}

	@Info("Capitalises the first letter of the string. If ignoreSpecial is true, it will also capitalise articles and prepositions")
	static String toTitleCase(String s, boolean ignoreSpecial) {
		if (s.isEmpty()) {
			return "";
		} else if (!ignoreSpecial && ALWAYS_LOWER_CASE.contains(s)) {
			return s;
		} else if (s.length() == 1) {
			return s.toUpperCase(Locale.ROOT);
		}

		char[] chars = s.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		return new String(chars);
	}

	static String stripIdForEvent(ResourceLocation id) {
		return stripEventName(id.toString());
	}

	static String getUniqueId(JsonElement json) {
		return getUniqueId(json, Function.identity());
	}

	static <T> String getUniqueId(T input, Function<T, JsonElement> toJson) {
		return JsonIO.getJsonHashString(toJson.apply(input));
	}

	static String stripEventName(String s) {
		return s.replaceAll("[/:]", ".").replace('-', '_');
	}
}
