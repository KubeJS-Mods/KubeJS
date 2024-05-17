package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.typings.desc.DescriptionContext;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
import dev.latvian.mods.kubejs.util.UtilsJS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RecipeComponentBuilder implements RecipeComponent<RecipeComponentBuilderMap> {
	public final List<RecipeKey<?>> keys;
	public Predicate<Set<String>> hasPriority;
	public ComponentRole role = ComponentRole.OTHER;

	public RecipeComponentBuilder(int init) {
		this.keys = new ArrayList<>(init);
	}

	public RecipeComponentBuilder add(RecipeKey<?> key) {
		keys.add(key);
		return this;
	}

	public RecipeComponentBuilder hasPriority(Predicate<Set<String>> hasPriority) {
		this.hasPriority = hasPriority;
		return this;
	}

	public RecipeComponentBuilder inputRole() {
		this.role = ComponentRole.INPUT;
		return this;
	}

	public RecipeComponentBuilder outputRole() {
		this.role = ComponentRole.OUTPUT;
		return this;
	}

	public RecipeComponentBuilder createCopy() {
		var copy = new RecipeComponentBuilder(keys.size());
		copy.keys.addAll(keys);
		copy.hasPriority = hasPriority;
		copy.role = role;
		return copy;
	}

	@Override
	public ComponentRole role() {
		return role;
	}

	@Override
	public String componentType() {
		return "builder";
	}

	@Override
	public Class<?> componentClass() {
		return RecipeComponentBuilderMap.class;
	}

	@Override
	public TypeDescJS constructorDescription(DescriptionContext ctx) {
		var obj = TypeDescJS.object(keys.size());

		for (var key : keys) {
			obj.add(key.name, key.component.constructorDescription(ctx), key.optional());
		}

		return obj;
	}

	@Override
	public JsonElement write(KubeRecipe recipe, RecipeComponentBuilderMap value) {
		var json = new JsonObject();

		for (var val : value.holders) {
			if (val.value != null) {
				var vc = new RecipeComponentValue<>(val.key, val.getIndex());
				vc.value = UtilsJS.cast(val.value);
				val.key.component.writeToJson(recipe, UtilsJS.cast(vc), json);
			}
		}

		return json;
	}

	@Override
	public RecipeComponentBuilderMap read(KubeRecipe recipe, Object from) {
		var value = new RecipeComponentBuilderMap(this);

		if (from instanceof JsonObject json) {
			for (var holder : value.holders) {
				holder.key.component.readFromJson(recipe, UtilsJS.cast(holder), json);

				if (!holder.key.optional() && holder.value == null) {
					throw new IllegalArgumentException("Missing required key '" + holder.key + "'!");
				}
			}
		} else if (from instanceof Map<?, ?> map) {
			for (var holder : value.holders) {
				holder.key.component.readFromMap(recipe, UtilsJS.cast(holder), map);

				if (!holder.key.optional() && holder.value == null) {
					throw new IllegalArgumentException("Missing required key '" + holder.key + "'!");
				}
			}
		} else {
			throw new IllegalArgumentException("Expected JSON object!");
		}

		return value;
	}

	@Override
	public boolean hasPriority(KubeRecipe recipe, Object from) {
		if (from instanceof Map m) {
			if (hasPriority != null) {
				return hasPriority.test(m.keySet());
			} else {
				for (var key : keys) {
					if (!key.optional() && !m.containsKey(key.name)) {
						return false;
					}
				}

				return true;
			}
		} else if (from instanceof JsonObject json) {
			if (hasPriority != null) {
				return hasPriority.test(json.keySet());
			} else {
				for (var key : keys) {
					if (!key.optional() && !json.has(key.name)) {
						return false;
					}
				}

				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean isInput(KubeRecipe recipe, RecipeComponentBuilderMap value, ReplacementMatch match) {
		for (var e : value.holders) {
			if (e.isInput(recipe, match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public RecipeComponentBuilderMap replaceInput(KubeRecipe recipe, RecipeComponentBuilderMap original, ReplacementMatch match, InputReplacement with) {
		for (var e : original.holders) {
			if (e.replaceInput(recipe, match, with)) {
				original.hasChanged = true;
			}
		}

		return original;
	}

	@Override
	public boolean isOutput(KubeRecipe recipe, RecipeComponentBuilderMap value, ReplacementMatch match) {
		for (var e : value.holders) {
			if (e.isOutput(recipe, match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public RecipeComponentBuilderMap replaceOutput(KubeRecipe recipe, RecipeComponentBuilderMap original, ReplacementMatch match, OutputReplacement with) {
		for (var e : original.holders) {
			if (e.replaceOutput(recipe, match, with)) {
				original.hasChanged = true;
			}
		}

		return original;
	}

	@Override
	public String toString() {
		return keys.stream().map(RecipeKey::toString).collect(Collectors.joining(",", "builder{", "}"));
	}

	@Override
	public boolean checkValueHasChanged(RecipeComponentBuilderMap oldValue, RecipeComponentBuilderMap newValue) {
		return newValue.hasChanged;
	}
}
