package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.KubeJSCodecs;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;

public class CharacterComponent extends SimpleRecipeComponent<Character> {
	public static final CharacterComponent CHARACTER = new CharacterComponent();

	public CharacterComponent() {
		super("character", KubeJSCodecs.CHARACTER, TypeInfo.CHARACTER);
	}

	@Override
	public boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
		return from instanceof Character || from instanceof CharSequence || from instanceof JsonPrimitive json && json.isString();
	}

	@Override
	public boolean isEmpty(Character value) {
		return value == '\0';
	}
}
