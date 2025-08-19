package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.codec.KubeJSCodecs;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;

public class CharacterComponent extends SimpleRecipeComponent<Character> {
	public static final RecipeComponentType<Character> CHARACTER = RecipeComponentType.unit(KubeJS.id("character"), CharacterComponent::new);

	public CharacterComponent(RecipeComponentType<?> type) {
		super(type, KubeJSCodecs.CHARACTER, TypeInfo.CHARACTER);
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
