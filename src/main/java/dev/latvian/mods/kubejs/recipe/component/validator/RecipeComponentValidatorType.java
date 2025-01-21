package dev.latvian.mods.kubejs.recipe.component.validator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.KubeJSCodecs;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.Lazy;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public record RecipeComponentValidatorType<T extends RecipeComponentValidator>(ResourceLocation id, MapCodec<T> codec) {
	public static final Lazy<Map<ResourceLocation, RecipeComponentValidatorType<?>>> TYPES = Lazy.of(() -> {
		var map = new HashMap<ResourceLocation, RecipeComponentValidatorType<?>>();
		KubeJSPlugins.forEachPlugin(type -> map.put(type.id, type), KubeJSPlugin::registerRecipeComponentValidatorTypes);
		return map;
	});

	public static final Codec<RecipeComponentValidatorType<?>> CODEC = KubeJSCodecs.KUBEJS_ID.xmap(s -> TYPES.get().get(s), RecipeComponentValidatorType::id);
}
