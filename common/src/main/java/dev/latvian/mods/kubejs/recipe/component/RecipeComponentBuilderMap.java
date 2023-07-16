package dev.latvian.mods.kubejs.recipe.component;

import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.util.UtilsJS;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class RecipeComponentBuilderMap extends AbstractMap<RecipeKey<?>, Object> {
	public final RecipeComponentValue<?>[] holders;
	private Set<Entry<RecipeKey<?>, Object>> holderSet;
	public boolean hasChanged;

	public RecipeComponentBuilderMap(RecipeComponentBuilder builder) {
		this.holders = new RecipeComponentValue[builder.keys.size()];

		for (int i = 0; i < holders.length; i++) {
			this.holders[i] = new RecipeComponentValue<>(builder.keys.get(i), i);
		}

		this.hasChanged = false;
	}

	public RecipeComponentBuilderMap(RecipeComponentValue<?>[] holders) {
		this.holders = new RecipeComponentValue[holders.length];

		for (int i = 0; i < holders.length; i++) {
			this.holders[i] = holders[i].copy();
		}
	}

	@NotNull
	@Override
	public Set<Map.Entry<RecipeKey<?>, Object>> entrySet() {
		if (holderSet == null) {
			holderSet = UtilsJS.cast(Set.of(holders));
		}

		return holderSet;
	}

	@Override
	public Object put(RecipeKey<?> key, Object value) {
		for (var holder : holders) {
			if (holder.key == key) {
				return holder.setValue(UtilsJS.cast(value));
			}
		}

		throw new IllegalArgumentException("Key " + key + " is not in this map!");
	}

	@Override
	public Object get(Object key) {
		for (var holder : holders) {
			if (holder.key == key) {
				return holder.value;
			}
		}

		return null;
	}

	@Override
	public Object getOrDefault(Object key, Object defaultValue) {
		var v = get(key);
		return v == null ? defaultValue : v;
	}

	@Override
	public int hashCode() {
		int i = 1;

		for (var holder : holders) {
			i = 31 * i + holder.key.hashCode();
			i = 31 * i + Objects.hashCode(holder.value);
		}

		return i;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (o instanceof RecipeComponentBuilderMap map) {
			if (holders.length != map.holders.length) {
				return false;
			} else {
				for (int i = 0; i < holders.length; i++) {
					if (holders[i].key != map.holders[i].key || holders[i].key.component.checkValueHasChanged(UtilsJS.cast(holders[i].value), UtilsJS.cast(map.holders[i].value))) {
						return false;
					}
				}

				return true;
			}
		} else {
			return false;
		}
	}
}