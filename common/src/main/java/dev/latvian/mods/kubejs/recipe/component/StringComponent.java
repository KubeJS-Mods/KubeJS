package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.function.Predicate;

public record StringComponent(String error, Predicate<String> predicate) implements RecipeComponent<String> {
	public static final RecipeComponent<String> ANY = new StringComponent("", s -> true);
	public static final RecipeComponent<String> DEFAULT_ANY = ANY.optional("");
	public static final RecipeComponent<String> NON_EMPTY = new StringComponent("can't be empty", s -> !s.isEmpty());
	public static final RecipeComponent<String> NON_BLANK = new StringComponent("can't be blank", s -> !s.isBlank());

	public static final RecipeComponent<Character> CHARACTER = new RecipeComponent<>() {
		@Override
		public String componentType() {
			return "char";
		}

		@Override
		public JsonElement write(Character value) {
			return new JsonPrimitive(value);
		}

		@Override
		public Character read(Object from) {
			return from instanceof Character c ? c : String.valueOf(from).charAt(0);
		}
	};

	@Override
	public String componentType() {
		return "string";
	}

	@Override
	public JsonPrimitive write(String value) {
		return new JsonPrimitive(value);
	}

	@Override
	public String read(Object from) {
		var str = from instanceof JsonPrimitive json ? json.getAsString() : String.valueOf(from);

		if (!predicate.test(str)) {
			if (error.isEmpty()) {
				throw new IllegalArgumentException("Invalid string '" + str + "'");
			} else {
				throw new IllegalArgumentException("Invalid string '" + str + "': " + error);
			}
		}

		return str;
	}
}
