package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.RecipeJS;

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
	public JsonObject description(RecipeJS recipe) {
		var obj = new JsonObject();
		obj.addProperty("type", componentType());

		var d = component.write(recipe, defaultValue);

		if (d != null) {
			obj.add("default_value", d);
		}

		obj.add("component", component.description(recipe));
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
		try {
			RecipeComponentWithParent.super.readJson(value, json);
		} catch (MissingComponentException ignored) {
		}
	}
}
