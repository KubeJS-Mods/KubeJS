package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
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

public class RecipeComponentBuilder implements RecipeComponent<RecipeComponentBuilder.RCBHolder[]> {
	public record RCBHolder(RecipeKey<?> key, Object value) {
	}

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
		return RCBHolder[].class;
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
	public JsonElement write(RecipeJS recipe, RCBHolder[] value) {
		var json = new JsonObject();

		for (var val : value) {
			if (val.value != null) {
				var vc = new RecipeComponentValue<>(recipe, val.key);
				vc.value = UtilsJS.cast(val.value);
				val.key.component.writeToJson(UtilsJS.cast(vc), json);
			}
		}

		return json;
	}

	@Override
	public RCBHolder[] read(RecipeJS recipe, Object from) {
		var values = new RCBHolder[keys.size()];

		if (from instanceof JsonObject json) {
			for (int i = 0; i < values.length; i++) {
				var vc = new RecipeComponentValue<>(recipe, keys.get(i));
				vc.key.component.readFromJson(UtilsJS.cast(vc), json);
				values[i] = new RCBHolder(keys.get(i), vc.value);
			}
		} else if (from instanceof Map<?, ?> map) {
			for (int i = 0; i < values.length; i++) {
				var vc = new RecipeComponentValue<>(recipe, keys.get(i));
				vc.key.component.readFromMap(UtilsJS.cast(vc), map);
				values[i] = new RCBHolder(keys.get(i), vc.value);
			}
		} else {
			throw new IllegalArgumentException("Expected JSON object!");
		}

		return values;
	}

	@Override
	public boolean hasPriority(RecipeJS recipe, Object from) {
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
	public boolean isInput(RecipeJS recipe, RCBHolder[] value, ReplacementMatch match) {
		for (var entry : value) {
			if (entry.value != null && entry.key.component.role().isInput() && entry.key.component.isInput(recipe, UtilsJS.cast(entry.value), match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public RCBHolder[] replaceInput(RecipeJS recipe, RCBHolder[] original, ReplacementMatch match, InputReplacement with) {
		var arr = original;

		for (int i = 0; i < original.length; i++) {
			if (original[i].value != null && original[i].key.component.role().isInput()) {
				var r = original[i].key.component.replaceInput(recipe, UtilsJS.cast(original[i].value()), match, with);

				if (r != original[i].value()) {
					if (arr == original) {
						arr = new RCBHolder[original.length];
						System.arraycopy(original, 0, arr, 0, i);
					}

					arr[i] = new RCBHolder(original[i].key(), r);
				}
			}
		}

		return arr;
	}

	@Override
	public boolean isOutput(RecipeJS recipe, RCBHolder[] value, ReplacementMatch match) {
		for (var entry : value) {
			if (entry.value != null && entry.key.component.role().isOutput() && entry.key.component.isOutput(recipe, UtilsJS.cast(entry.value), match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public RCBHolder[] replaceOutput(RecipeJS recipe, RCBHolder[] original, ReplacementMatch match, OutputReplacement with) {
		var arr = original;

		for (int i = 0; i < original.length; i++) {
			if (original[i].value != null && original[i].key.component.role().isOutput()) {
				var r = original[i].key.component.replaceOutput(recipe, UtilsJS.cast(original[i].value()), match, with);

				if (r != original[i].value()) {
					if (arr == original) {
						arr = new RCBHolder[original.length];
						System.arraycopy(original, 0, arr, 0, i);
					}

					arr[i] = new RCBHolder(original[i].key(), r);
				}
			}
		}

		return arr;
	}

	@Override
	public String toString() {
		return keys.stream().map(RecipeKey::toString).collect(Collectors.joining(",", "builder{", "}"));
	}
}
