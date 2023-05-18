package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public record OptionalRecipeComponent<T>(RecipeComponent<T> component, T defaultValue, boolean alwaysWrite) implements RecipeComponent<T> {
	@Override
	public String componentType() {
		return "optional";
	}

	@Override
	public JsonObject description() {
		var obj = new JsonObject();
		obj.addProperty("type", componentType());

		var d = component.write(defaultValue);

		if (d != null) {
			obj.add("default_value", d);
		}

		if (alwaysWrite) {
			obj.addProperty("always_write", true);
		}

		obj.add("component", component.description());
		return obj;
	}

	@Override
	public RecipeComponentType getType() {
		return component.getType();
	}

	@Override
	public JsonElement write(T value) {
		return component.write(value);
	}

	@Override
	public T read(Object from) {
		return component.read(from);
	}
}
