package dev.latvian.mods.kubejs.recipe.ingredientaction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.Lazy;

import java.util.HashMap;
import java.util.Map;

public record IngredientActionType(String id, MapCodec<? extends IngredientAction> codec) {
	public static final Lazy<Map<String, IngredientActionType>> TYPES = Lazy.of(() -> {
		var map = new HashMap<String, IngredientActionType>();
		KubeJSPlugins.forEachPlugin(type -> map.put(type.id, type), KubeJSPlugin::registerIngredientActionTypes);
		return map;
	});

	public static final Codec<IngredientActionType> CODEC = Codec.STRING.xmap(s -> TYPES.get().get(s), IngredientActionType::id);
}
