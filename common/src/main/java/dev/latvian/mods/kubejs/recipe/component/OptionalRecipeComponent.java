package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonObject;

public record OptionalRecipeComponent<T>(RecipeComponent<T> component, T defaultValue, boolean alwaysWrite) implements RecipeComponentWithParent<T> {
	@Override
	public RecipeComponent<T> parentComponent() {
		return component;
	}

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
	public String toString() {
		return component + "?";
	}
}
