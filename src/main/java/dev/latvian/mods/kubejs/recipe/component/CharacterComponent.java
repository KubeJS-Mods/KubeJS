package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.codec.KubeJSCodecs;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.Nullable;

public class CharacterComponent extends SimpleRecipeComponent<Character> {
	public static final CharacterComponent CHARACTER = new CharacterComponent(RecipeComponentType.builtin("character"));

	public CharacterComponent(ResourceKey<RecipeComponentType<?>> type) {
		super(type, KubeJSCodecs.CHARACTER, TypeInfo.CHARACTER);
	}

	@Override
	public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
		return from instanceof Character || from instanceof CharSequence || from instanceof JsonPrimitive json && json.isString();
	}

	@Override
	public boolean isEmpty(Character value) {
		return value == '\0';
	}

	@Override
	public String toString(OpsContainer ops, Character value) {
		return "'" + value + "'";
	}
}
