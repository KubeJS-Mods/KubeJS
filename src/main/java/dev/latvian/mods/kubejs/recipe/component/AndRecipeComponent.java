package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonArray;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.rhino.type.JSFixedArrayTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import org.apache.commons.lang3.tuple.Pair;

public record AndRecipeComponent<A, B>(RecipeComponent<A> a, RecipeComponent<B> b) implements RecipeComponent<Pair<A, B>> {
	@Override
	public String componentType() {
		return "and";
	}

	@Override
	public TypeInfo typeInfo() {
		return new JSFixedArrayTypeInfo(a.typeInfo(), b.typeInfo());
	}

	@Override
	public ComponentRole role() {
		if (a.role().isOther()) {
			return b.role();
		}

		return a.role();
	}

	@Override
	public JsonArray write(KubeRecipe recipe, Pair<A, B> value) {
		var json = new JsonArray();
		json.add(a.write(recipe, value.getLeft()));
		json.add(b.write(recipe, value.getRight()));
		return json;
	}

	@Override
	public Pair<A, B> read(KubeRecipe recipe, Object from) {
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
