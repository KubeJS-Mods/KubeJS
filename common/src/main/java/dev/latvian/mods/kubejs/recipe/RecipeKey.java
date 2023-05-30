package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;

import java.util.ArrayList;
import java.util.List;

public final class RecipeKey<T> {
	private final RecipeComponent<T> component;
	private int index;
	private final String name;
	private final List<String> altNames;
	private String preferred;

	public RecipeKey(RecipeComponent<T> component, String name) {
		this.component = component;
		this.index = -1;
		this.name = name;
		this.altNames = new ArrayList<>(0);
		this.preferred = name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return name + ":" + component;
	}

	public RecipeComponent<T> component() {
		return component;
	}

	public int index() {
		return index;
	}

	public void index(int index) {
		if (this.index == -1) {
			this.index = index;
		} else {
			throw new IllegalStateException("You can't reuse the same RecipeKey in more than once!");
		}
	}

	public String name() {
		return name;
	}

	public RecipeKey<T> alt(String name) {
		altNames.add(name);
		return this;
	}

	public RecipeKey<T> alt(String... names) {
		altNames.addAll(List.of(names));
		return this;
	}

	public List<String> altNames() {
		return altNames;
	}

	/**
	 * No real function, only used for generating typings / docs
	 */
	public RecipeKey<T> preferred(String name) {
		preferred = name;
		return this;
	}

	public String preferred() {
		return preferred;
	}
}
