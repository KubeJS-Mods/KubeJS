package dev.latvian.mods.kubejs.recipe.ingredientaction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.codec.KubeJSCodecs;
import dev.latvian.mods.kubejs.codec.KubeJSStreamCodecs;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.Lazy;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record IngredientActionType<T extends IngredientAction>(ResourceLocation id, MapCodec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
	public static final Lazy<Map<ResourceLocation, IngredientActionType<?>>> TYPES = Lazy.map(map -> KubeJSPlugins.forEachPlugin(type -> map.put(type.id, type), KubeJSPlugin::registerIngredientActionTypes));

	public static final Codec<IngredientActionType<?>> CODEC = KubeJSCodecs.KUBEJS_ID.comapFlatMap(id -> {
		var type = TYPES.get().get(id);

		if (type != null) {
			return DataResult.success(type);
		} else {
			return DataResult.error(() -> "Unknown ingredient action type: " + id);
		}
	}, IngredientActionType::id);

	public static final StreamCodec<RegistryFriendlyByteBuf, IngredientActionType<?>> STREAM_CODEC = KubeJSStreamCodecs.KUBEJS_ID.map(s -> TYPES.get().get(s), IngredientActionType::id);

	public IngredientActionType(ResourceLocation id, MapCodec<T> codec) {
		this(id, codec, ByteBufCodecs.fromCodecWithRegistries(codec.codec()));
	}
}
