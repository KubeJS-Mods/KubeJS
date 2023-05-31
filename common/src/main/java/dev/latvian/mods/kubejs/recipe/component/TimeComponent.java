package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.util.UtilsJS;

public class TimeComponent implements RecipeComponent<Long> {
	public static final TimeComponent TICKS = new TimeComponent();

	@Override
	public String componentType() {
		return "ticks";
	}

	@Override
	public Class<?> componentClass() {
		return Long.class;
	}

	@Override
	public JsonElement write(RecipeJS recipe, Long value) {
		return new JsonPrimitive(value);
	}

	@Override
	public boolean hasPriority(RecipeJS recipe, Object from) {
		return from instanceof Number || from instanceof JsonPrimitive json && json.isNumber();
	}

	@Override
	public Long read(RecipeJS recipe, Object from) {
		return UtilsJS.getTickDuration(from);
	}

	@Override
	public String toString() {
		return componentType();
	}
}
