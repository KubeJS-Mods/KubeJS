package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.resources.ResourceLocation;

public record StringComponent(RecipeComponentType<?> type, Codec<String> stringCodec, boolean allowEmpty) implements RecipeComponent<String> {
	public static final RecipeComponentType<String> ANY = RecipeComponentType.unit(KubeJS.id("string"), type -> new StringComponent(type, Codec.STRING, true));
	public static final RecipeComponentType<String> NON_EMPTY = RecipeComponentType.unit(KubeJS.id("non_empty_string"), type -> new StringComponent(type, Codec.STRING, false));
	public static final RecipeComponentType<String> ID = RecipeComponentType.unit(KubeJS.id("id"), type -> new StringComponent(type, Codec.STRING.validate(s -> ResourceLocation.read(s).map(ResourceLocation::toString)), false));

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
	public boolean isEmpty(String value) {
		return value.isEmpty();
	}

	@Override
	public String toString() {
		return type.toString();
	}
}
