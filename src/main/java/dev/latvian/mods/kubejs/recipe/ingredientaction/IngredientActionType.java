package dev.latvian.mods.kubejs.recipe.ingredientaction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.KubeJSCodecs;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.Lazy;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public record IngredientActionType<T extends IngredientAction>(ResourceLocation id, MapCodec<T> codec) {
	public static final Lazy<Map<ResourceLocation, IngredientActionType<?>>> TYPES = Lazy.of(() -> {
		var map = new HashMap<ResourceLocation, IngredientActionType<?>>();
		KubeJSPlugins.forEachPlugin(type -> map.put(type.id, type), KubeJSPlugin::registerIngredientActionTypes);
		return map;
	});

	public static final Codec<IngredientActionType<?>> CODEC = KubeJSCodecs.KUBEJS_ID.xmap(s -> TYPES.get().get(s), IngredientActionType::id);
}
