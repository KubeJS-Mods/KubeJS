package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.ScriptRuntime;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record StringComponent(ResourceKey<RecipeComponentType<?>> type, Codec<String> codec, boolean allowEmpty) implements RecipeComponent<String> {
	public static final StringComponent STRING = new StringComponent(RecipeComponentType.builtin("string"), ExtraCodecs.NON_EMPTY_STRING, false);
	public static final StringComponent OPTIONAL_STRING = new StringComponent(RecipeComponentType.builtin("optional_string"), Codec.STRING, true);
	public static final StringComponent ID = new StringComponent(RecipeComponentType.builtin("id"), Codec.STRING.validate(s -> Identifier.read(s).map(Identifier::toString)), false);

	@Override
	public TypeInfo typeInfo() {
		return TypeInfo.STRING;
	}

	@Override
	public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
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
