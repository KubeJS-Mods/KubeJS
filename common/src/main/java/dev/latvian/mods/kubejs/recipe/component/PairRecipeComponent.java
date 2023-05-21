package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.tuple.Pair;

public record PairRecipeComponent<A, B>(RecipeComponent<A> a, RecipeComponent<B> b) implements RecipeComponent<Pair<A, B>> {
	public static <A, B> PairRecipeComponent<A, B> of(RecipeComponent<A> a, RecipeComponent<B> b) {
		return new PairRecipeComponent<>(a, b);
	}

	@Override
	public String componentType() {
		return "pair";
	}

	@Override
	public JsonObject description() {
		var obj = new JsonObject();
		obj.addProperty("type", componentType());
		var arr = new JsonArray();
		arr.add(a.description());
		arr.add(b.description());
		obj.add("members", arr);
		return obj;
	}

	@Override
	public RecipeComponentType getType() {
		if (a.getType() == RecipeComponentType.OTHER) {
			return b.getType();
		}

		return a.getType();
	}

	@Override
	public JsonArray write(Pair<A, B> value) {
		var json = new JsonArray();
		json.add(a.write(value.getLeft()));
		json.add(b.write(value.getRight()));
		return json;
	}

	@Override
	public Pair<A, B> read(Object from) {
		if (from instanceof Iterable<?> iterable) {
			var itr = iterable.iterator();
			return Pair.of(a.read(itr.next()), b.read(itr.next()));
		}

		throw new IllegalArgumentException("Expected JSON array!");
	}
}
