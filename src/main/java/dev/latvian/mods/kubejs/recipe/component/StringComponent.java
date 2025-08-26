package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.ScriptRuntime;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.ArrayList;
import java.util.List;

public record StringComponent(RecipeComponentType<?> type, Codec<String> codec, boolean allowEmpty) implements RecipeComponent<String> {
	public static final RecipeComponentType<String> STRING = RecipeComponentType.unit(KubeJS.id("string"), type -> new StringComponent(type, ExtraCodecs.NON_EMPTY_STRING, false));
	public static final RecipeComponentType<String> OPTIONAL_STRING = RecipeComponentType.unit(KubeJS.id("optional_string"), type -> new StringComponent(type, Codec.STRING, true));
	public static final RecipeComponentType<String> ID = RecipeComponentType.unit(KubeJS.id("id"), type -> new StringComponent(type, Codec.STRING.validate(s -> ResourceLocation.read(s).map(ResourceLocation::toString)), false));

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

	@Override
	public List<Character> spread(String value) {
		if (value.isEmpty()) {
			return List.of();
		}

		var list = new ArrayList<Character>(value.length());

		for (char c : value.toCharArray()) {
			list.add(c);
		}

		return list;
	}

	@Override
	public String toString(OpsContainer ops, String value) {
		return ScriptRuntime.escapeAndWrapString(value);
	}
}
