package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.schema.DynamicRecipeComponent;
import dev.latvian.mods.kubejs.typings.desc.DescriptionContext;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.NativeJavaClass;

import java.util.function.BiFunction;
import java.util.function.Function;

public record EnumComponent<T extends Enum<T>>(Class<T> enumType, Function<T, String> toStringFunc, BiFunction<Class<T>, String, T> toEnumFunc) implements RecipeComponent<T> {
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static final DynamicRecipeComponent DYNAMIC = new DynamicRecipeComponent(TypeDescJS.object().add("class", TypeDescJS.STRING.or(DescriptionContext.DEFAULT.javaType(Class.class))), (ctx, scope, args) -> {
		Object o = args.get("class");
		Class clazz;
		if (o instanceof NativeJavaClass njc) {
			clazz = njc.getClassObject();
		} else if (o instanceof Class c) {
			clazz = c;
		} else {
			try {
				return new EnumComponent<>((Class) Class.forName(String.valueOf(o)));
			} catch (ClassNotFoundException e) {
				throw new RecipeExceptionJS("Error loading class " + o + " for EnumComponent", e);
			}
		}
		if (!clazz.isEnum()) {
			throw new RecipeExceptionJS("Class " + clazz.getTypeName() + " is not an enum!");
		}
		return new EnumComponent<>(clazz);
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
		this(enumType, UtilsJS.cast(DEFAULT_TO_STRING), UtilsJS.cast(DEFAULT_TO_ENUM));
	}

	@Override
	public String componentType() {
		return "enum";
	}

	@Override
	public Class<?> componentClass() {
		return enumType;
	}

	@Override
	public JsonPrimitive write(RecipeJS recipe, T value) {
		return new JsonPrimitive(toStringFunc.apply(value));
	}

	@Override
	@SuppressWarnings("unchecked")
	public T read(RecipeJS recipe, Object from) {
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
