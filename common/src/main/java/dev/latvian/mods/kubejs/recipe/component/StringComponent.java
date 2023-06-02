package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.typings.desc.DescriptionContext;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;

import java.util.function.Predicate;

public record StringComponent(String error, Predicate<String> predicate) implements RecipeComponent<String> {
	public static final RecipeComponent<String> ANY = new StringComponent("", s -> true);
	public static final RecipeComponent<String> NON_EMPTY = new StringComponent("can't be empty", s -> !s.isEmpty());
	public static final RecipeComponent<String> NON_BLANK = new StringComponent("can't be blank", s -> !s.isBlank());

	public static final RecipeComponent<Character> CHARACTER = new RecipeComponent<>() {
		@Override
		public String componentType() {
			return "char";
		}

		@Override
		public Class<?> componentClass() {
			return Character.class;
		}

		@Override
		public TypeDescJS constructorDescription(DescriptionContext ctx) {
			return TypeDescJS.STRING;
		}

		@Override
		public JsonElement write(RecipeJS recipe, Character value) {
			return new JsonPrimitive(value);
		}

		@Override
		public Character read(RecipeJS recipe, Object from) {
			return from instanceof Character c ? c : String.valueOf(from).charAt(0);
		}

		@Override
		public String toString() {
			return componentType();
		}
	};

	@Override
	public String componentType() {
		return "string";
	}

	@Override
	public Class<?> componentClass() {
		return String.class;
	}

	@Override
	public TypeDescJS constructorDescription(DescriptionContext ctx) {
		return TypeDescJS.STRING;
	}

	@Override
	public JsonPrimitive write(RecipeJS recipe, String value) {
		return new JsonPrimitive(value);
	}

	@Override
	public String read(RecipeJS recipe, Object from) {
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

	@Override
	public boolean hasPriority(RecipeJS recipe, Object from) {
		return from instanceof CharSequence || from instanceof JsonPrimitive json && json.isString();
	}

	@Override
	public String toString() {
		return componentType();
	}
}
