package dev.latvian.mods.kubejs.recipe.component;

import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.rhino.Context;

import java.util.List;

public class StringGridComponent extends SimpleRecipeComponent<List<String>> {
	private static final RecipeComponent<List<String>> PARENT = StringComponent.NON_EMPTY.asList();
	public static final StringGridComponent STRING_GRID = new StringGridComponent();

	private StringGridComponent() {
		super("string_grid", PARENT.codec(), PARENT.typeInfo());
	}

	@Override
	public boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
		return from instanceof Iterable<?> || from != null && from.getClass().isArray();
	}

	@Override
	public List<String> wrap(Context cx, KubeRecipe recipe, Object from) {
		return PARENT.wrap(cx, recipe, from);
	}
}
