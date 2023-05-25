package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class RecipeKey<T> {
	public record KK(RecipeComponent<?> component, int index, String name) {
		@Override
		public boolean equals(Object obj) {
			return obj instanceof KK kk && component == kk.component && index == kk.index && name.equals(kk.name);
		}
	}

	public static final Map<KK, RecipeKey<?>> ALL_KEYS = Collections.synchronizedMap(new HashMap<>());

	@SuppressWarnings("unchecked")
	public static <T> RecipeKey<T> of(RecipeComponent<T> component, int index, String name) {
		return (RecipeKey<T>) ALL_KEYS.computeIfAbsent(new KK(component, index, name), RecipeKey::new);
	}

	private final RecipeComponent<T> component;
	private final int index;
	private final String name;

	@SuppressWarnings("unchecked")
	private RecipeKey(KK kk) {
		this.component = (RecipeComponent<T>) kk.component;
		this.index = kk.index;
		this.name = kk.name;
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

	public String name() {
		return name;
	}
}
