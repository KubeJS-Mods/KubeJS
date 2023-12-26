package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.typings.desc.DescriptionContext;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;

public class BooleanComponent implements RecipeComponent<Boolean> {
	public static final RecipeComponent<Boolean> BOOLEAN = new BooleanComponent();

	@Override
	public String componentType() {
		return "boolean";
	}

	@Override
	public Class<?> componentClass() {
		return Boolean.class;
	}

	@Override
	public TypeDescJS constructorDescription(DescriptionContext ctx) {
		return TypeDescJS.BOOLEAN;
	}

	@Override
	public JsonElement write(RecipeJS recipe, Boolean value) {
		return new JsonPrimitive(value);
	}

	@Override
	public Boolean read(RecipeJS recipe, Object from) {
		if (from instanceof Boolean n) {
			return n;
		} else if (from instanceof JsonPrimitive json) {
			return json.getAsBoolean();
		} else if (from instanceof CharSequence) {
			return Boolean.parseBoolean(from.toString());
		}

		throw new IllegalStateException("Expected a boolean!");
	}

	@Override
	public boolean hasPriority(RecipeJS recipe, Object from) {
		return from instanceof Boolean || from instanceof JsonPrimitive json && json.isBoolean();
	}

	@Override
	public String toString() {
		return componentType();
	}
}
