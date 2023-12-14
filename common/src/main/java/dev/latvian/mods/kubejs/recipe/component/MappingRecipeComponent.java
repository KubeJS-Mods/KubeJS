package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import org.jetbrains.annotations.Nullable;

import java.util.function.UnaryOperator;

public class MappingRecipeComponent<T> implements RecipeComponentWithParent<T> {

	private final RecipeComponent<T> parent;
	private final UnaryOperator<Object> mappingTo;
	private final UnaryOperator<JsonElement> mappingFrom;

	public MappingRecipeComponent(RecipeComponent<T> parent, UnaryOperator<Object> mappingTo, UnaryOperator<JsonElement> mappingFrom) {
		this.parent = parent;
		this.mappingTo = mappingTo;
		this.mappingFrom = mappingFrom;
	}


	@Override
	public T read(RecipeJS recipe, Object from) {
		return RecipeComponentWithParent.super.read(recipe, mappingTo.apply(from));
	}

	@Override
	public @Nullable JsonElement write(RecipeJS recipe, T value) {
		return mappingFrom.apply(RecipeComponentWithParent.super.write(recipe, value));
	}

	@Override
	public RecipeComponent<T> parentComponent() {
		return parent;
	}
}
