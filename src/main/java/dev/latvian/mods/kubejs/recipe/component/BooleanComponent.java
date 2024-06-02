package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;

public class BooleanComponent implements RecipeComponent<Boolean> {
	public static final RecipeComponent<Boolean> BOOLEAN = new BooleanComponent();

	@Override
	public Codec<Boolean> codec() {
		return Codec.BOOL;
	}

	@Override
	public TypeInfo typeInfo() {
		return TypeInfo.BOOLEAN;
	}

	@Override
	public Boolean wrap(Context cx, KubeRecipe recipe, Object from) {
		if (from instanceof Boolean n) {
			return n;
		} else if (from instanceof JsonPrimitive json) {
			return json.getAsBoolean();
		} else if (from instanceof CharSequence) {
			return Boolean.parseBoolean(from.toString());
		}

		throw new IllegalStateException("Expected a boolean!");
	}

	@Override
	public boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
		return from instanceof Boolean || from instanceof JsonPrimitive json && json.isBoolean();
	}

	@Override
	public String toString() {
		return "boolean";
	}
}
