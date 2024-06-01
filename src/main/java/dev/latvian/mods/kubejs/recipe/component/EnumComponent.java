package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.schema.DynamicRecipeComponent;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.rhino.type.JSObjectTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;

import java.util.function.BiFunction;
import java.util.function.Function;

public record EnumComponent<T extends Enum<T>>(Class<T> enumType, Function<T, String> toStringFunc, BiFunction<Class<T>, String, T> toEnumFunc) implements RecipeComponent<T> {
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static final DynamicRecipeComponent DYNAMIC = new DynamicRecipeComponent(JSObjectTypeInfo.of(
		new JSObjectTypeInfo.Field("class", TypeInfo.CLASS)
	), (ctx, scope, args) -> {
		var cname = args.get("class");

		try {
			if (cname == null) {
				throw new NullPointerException();
			}

			var clazz = ((KubeJSContext) ctx).loadJavaClass(cname);

			if (!clazz.isEnum()) {
				throw new RecipeExceptionJS("Class " + clazz.getTypeName() + " is not an enum!");
			}

			return new EnumComponent<>((Class) clazz);
		} catch (Exception ex) {
			throw new RecipeExceptionJS("Error loading class " + cname + " for EnumComponent", ex);
		}
	});
	private static final Function<Enum<?>, String> DEFAULT_TO_STRING = e -> e.name().toLowerCase();
	private static final BiFunction<Class<? extends Enum<?>>, String, Enum<?>> DEFAULT_TO_ENUM = (c, s) -> {
		for (var e : c.getEnumConstants()) {
			if (e.name().equalsIgnoreCase(s)) {
				return e;
			}
		}

		return null;
	};

	public EnumComponent(Class<T> enumType) {
		this(enumType, Cast.to(DEFAULT_TO_STRING), Cast.to(DEFAULT_TO_ENUM));
	}

	@Override
	public String componentType() {
		return "enum";
	}

	@Override
	public TypeInfo typeInfo() {
		return TypeInfo.of(enumType);
	}

	@Override
	public JsonPrimitive write(KubeRecipe recipe, T value) {
		return new JsonPrimitive(toStringFunc.apply(value));
	}

	@Override
	@SuppressWarnings("unchecked")
	public T read(KubeRecipe recipe, Object from) {
		if (enumType.isInstance(from)) {
			return (T) from;
		} else {
			var e = from == null ? null : toEnumFunc.apply(enumType, from instanceof JsonPrimitive j ? j.getAsString() : String.valueOf(from));

			if (e == null) {
				throw new RecipeExceptionJS("Enum value '" + from + "' of " + enumType.getName() + " not found");
			}

			return e;
		}
	}

	@Override
	public String toString() {
		return componentType();
	}
}
