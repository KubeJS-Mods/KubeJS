package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonObject;

public record OptionalRecipeComponent<T>(RecipeComponent<T> component, T defaultValue) implements RecipeComponentWithParent<T> {
	@Override
	public RecipeComponent<T> parentComponent() {
		return component;
	}

	@Override
	public T optionalValue() {
		return defaultValue;
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

		obj.add("component", component.description());
		return obj;
	}

	@Override
	public String toString() {
		return component + "?";
	}

	@Override
	public void writeJson(RecipeComponentValue<T> value, JsonObject json) {
		component.writeJson(value, json);
	}

	@Override
	public void readJson(RecipeComponentValue<T> value, JsonObject json) {
		if (json.has(value.key.name())) {
			component.readJson(value, json);
		} else {
			value.value = defaultValue;
		}
	}
}
