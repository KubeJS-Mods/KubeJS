package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.resources.ResourceLocation;

public record StringComponent(String name, Codec<String> stringCodec) implements RecipeComponent<String> {
	public static final RecipeComponent<String> ANY = new StringComponent("string", Codec.STRING);

	public static final RecipeComponent<String> NON_EMPTY = new StringComponent("non_empty_string", Codec.STRING.validate(s -> {
		if (s.isEmpty()) {
			return DataResult.error(() -> "can't be empty");
		}

		return DataResult.success(s);
	}));

	public static final RecipeComponent<String> NON_BLANK = new StringComponent("non_blank_string", Codec.STRING.validate(s -> {
		if (s.isBlank()) {
			return DataResult.error(() -> "can't be blank");
		}

		return DataResult.success(s);
	}));

	public static final RecipeComponent<String> ID = new StringComponent("id", Codec.STRING.validate(s -> {
		if (!ResourceLocation.isValidResourceLocation(s)) {
			return DataResult.error(() -> "invalid ID");
		}

		return DataResult.success(s);
	}));

	@Override
	public Codec<String> codec() {
		return stringCodec;
	}

	@Override
	public TypeInfo typeInfo() {
		return TypeInfo.STRING;
	}

	@Override
	public boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
		return from instanceof Character || from instanceof CharSequence || from instanceof JsonPrimitive json && json.isString();
	}

	@Override
	public String toString() {
		return name;
	}
}
