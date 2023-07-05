package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.typings.desc.DescriptionContext;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
import dev.latvian.mods.kubejs.util.UtilsJS;

public record TimeComponent(String name, long scale) implements RecipeComponent<Long> {
	public static final TimeComponent TICKS = new TimeComponent("ticks", 1L);
	public static final TimeComponent SECONDS = new TimeComponent("seconds", 20L);
	public static final TimeComponent MINUTES = new TimeComponent("minutes", 1200L);

	@Override
	public String componentType() {
		return name;
	}

	@Override
	public Class<?> componentClass() {
		return Long.class;
	}

	@Override
	public TypeDescJS constructorDescription(DescriptionContext ctx) {
		return TypeDescJS.NUMBER.or(TypeDescJS.STRING);
	}

	@Override
	public JsonElement write(RecipeJS recipe, Long value) {
		return new JsonPrimitive(value / scale);
	}

	@Override
	public boolean hasPriority(RecipeJS recipe, Object from) {
		return from instanceof Number || from instanceof JsonPrimitive json && json.isNumber();
	}

	@Override
	public Long read(RecipeJS recipe, Object from) {
		if (from instanceof Number) {
			return ((Number) from).longValue() * scale;
		}

		return UtilsJS.getTickDuration(from);
	}

	@Override
	public String toString() {
		return componentType();
	}
}
