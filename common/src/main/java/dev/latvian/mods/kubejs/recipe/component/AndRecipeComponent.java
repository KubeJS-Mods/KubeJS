package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import org.apache.commons.lang3.tuple.Pair;

public record AndRecipeComponent<A, B>(RecipeComponent<A> a, RecipeComponent<B> b) implements RecipeComponent<Pair<A, B>> {
	@Override
	public String componentType() {
		return "and";
	}

	@Override
	public JsonObject description(RecipeJS recipe) {
		var obj = new JsonObject();
		obj.addProperty("type", componentType());
		var arr = new JsonArray();
		arr.add(a.description(recipe));
		arr.add(b.description(recipe));
		obj.add("members", arr);
		return obj;
	}

	@Override
	public ComponentRole role() {
		if (a.role().isOther()) {
			return b.role();
		}

		return a.role();
	}

	@Override
	public Class<?> componentClass() {
		return Pair.class;
	}

	@Override
	public JsonArray write(RecipeJS recipe, Pair<A, B> value) {
		var json = new JsonArray();
		json.add(a.write(recipe, value.getLeft()));
		json.add(b.write(recipe, value.getRight()));
		return json;
	}

	@Override
	public Pair<A, B> read(RecipeJS recipe, Object from) {
		if (from instanceof Iterable<?> iterable) {
			var itr = iterable.iterator();
			return Pair.of(a.read(recipe, itr.next()), b.read(recipe, itr.next()));
		}

		throw new IllegalArgumentException("Expected JSON array!");
	}

	@Override
	public String toString() {
		return "{" + a + "&" + b + "}";
	}
}
