package dev.latvian.mods.kubejs.recipe.component;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.rhino.Context;

import java.util.List;

public class StringGridComponent extends SimpleRecipeComponent<List<String>> {
	private static final RecipeComponent<List<String>> PARENT = StringComponent.NON_EMPTY.instance().asList();
	public static final RecipeComponentType<List<String>> STRING_GRID = RecipeComponentType.unit(KubeJS.id("string_grid"), StringGridComponent::new);

	private StringGridComponent(RecipeComponentType<?> type) {
		super(type, "string_grid", PARENT.codec(), PARENT.typeInfo());
	}

	@Override
	public boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
		return from instanceof Iterable<?> || from != null && from.getClass().isArray();
	}

	@Override
	public List<String> wrap(Context cx, KubeRecipe recipe, Object from) {
		return PARENT.wrap(cx, recipe, from);
	}

	@Override
	public boolean isEmpty(List<String> value) {
		return value.isEmpty();
	}
}
