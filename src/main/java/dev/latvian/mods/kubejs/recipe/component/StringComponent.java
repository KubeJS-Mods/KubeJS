package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.schema.DynamicRecipeComponent;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.ScriptableObject;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.type.JSObjectTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Predicate;

public record StringComponent(String error, Predicate<String> predicate) implements RecipeComponent<String> {
	public static final RecipeComponent<String> ANY = new StringComponent("", s -> true);
	public static final RecipeComponent<String> NON_EMPTY = new StringComponent("can't be empty", s -> !s.isEmpty());
	public static final RecipeComponent<String> NON_BLANK = new StringComponent("can't be blank", s -> !s.isBlank());
	public static final RecipeComponent<String> ID = new StringComponent("invalid ID", ResourceLocation::isValidResourceLocation);

	public static final RecipeComponent<Character> CHARACTER = new RecipeComponent<>() {
		@Override
		public String componentType() {
			return "char";
		}

		@Override
		public TypeInfo typeInfo() {
			return TypeInfo.CHARACTER;
		}

		@Override
		public JsonElement write(KubeRecipe recipe, Character value) {
			return new JsonPrimitive(value);
		}

		@Override
		public Character read(KubeRecipe recipe, Object from) {
			return from instanceof Character c ? c : String.valueOf(from).charAt(0);
		}

		@Override
		public String toString() {
			return componentType();
		}
	};

	public static final DynamicRecipeComponent DYNAMIC = new DynamicRecipeComponent(JSObjectTypeInfo.of(
		new JSObjectTypeInfo.Field("error", TypeInfo.STRING, true),
		new JSObjectTypeInfo.Field("filter", TypeInfo.NONE)
	), (cx, scope, args) -> {
			var error = String.valueOf(Wrapper.unwrapped(args.getOrDefault("error", "invalid string")));
			var filter = args.get("filter") instanceof ScriptableObject obj ? cx.createInterfaceAdapter(TypeInfo.RAW_PREDICATE.withParams(TypeInfo.STRING), obj) : UtilsJS.ALWAYS_TRUE;
			return new StringComponent(error, (Predicate) filter);
		});

	@Override
	public String componentType() {
		return "string";
	}

	@Override
	public TypeInfo typeInfo() {
		return TypeInfo.STRING;
	}

	@Override
	public JsonPrimitive write(KubeRecipe recipe, String value) {
		return new JsonPrimitive(value);
	}

	@Override
	public String read(KubeRecipe recipe, Object from) {
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
	public boolean hasPriority(KubeRecipe recipe, Object from) {
		return from instanceof CharSequence || from instanceof JsonPrimitive json && json.isString();
	}

	@Override
	public String toString() {
		return componentType();
	}
}
