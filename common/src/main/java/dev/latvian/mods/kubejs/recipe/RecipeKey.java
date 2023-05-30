package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class RecipeKey<T> {
	private final RecipeComponent<T> component;
	private int index;
	private final String name;
	private final Set<String> names;
	private String preferred;
	private T optional;
	private boolean excluded;
	private boolean allowEmpty;

	public RecipeKey(RecipeComponent<T> component, String name) {
		this.component = component;
		this.index = -1;
		this.name = name;
		this.names = new LinkedHashSet<>(1);
		this.names.add(name);
		this.preferred = name;
		this.optional = null;
		this.excluded = false;
		this.allowEmpty = false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		var sb = new StringBuilder(name);

		if (optional != null) {
			sb.append('?');
		}

		sb.append(':');
		sb.append(component);
		return sb.toString();
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

	public RecipeKey<T> optional(T value) {
		optional = value;
		return this;
	}

	public T optional() {
		return optional;
	}

	public RecipeKey<T> alt(String name) {
		names.add(name);
		return this;
	}

	public RecipeKey<T> alt(String... names) {
		this.names.addAll(List.of(names));
		return this;
	}

	public Set<String> names() {
		return names;
	}

	/**
	 * No real function, only used for generating typings / docs
	 */
	public RecipeKey<T> preferred(String name) {
		if (!names.contains(name)) {
			throw new IllegalArgumentException("Name not found!");
		}

		preferred = name;
		return this;
	}

	public String preferred() {
		return preferred;
	}

	/**
	 * Excludes this key from auto-generated constructors
	 */
	public RecipeKey<T> setExcluded() {
		excluded = true;
		return this;
	}

	public boolean excluded() {
		return excluded;
	}

	public RecipeKey<T> setAllowEmpty() {
		allowEmpty = true;
		return this;
	}

	public boolean allowEmpty() {
		return allowEmpty;
	}
}
