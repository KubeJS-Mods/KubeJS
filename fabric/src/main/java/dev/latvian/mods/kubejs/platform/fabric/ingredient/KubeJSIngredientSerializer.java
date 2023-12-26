package dev.latvian.mods.kubejs.platform.fabric.ingredient;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public record KubeJSIngredientSerializer<T extends KubeJSIngredient>(ResourceLocation id, Codec<T> codec, Function<FriendlyByteBuf, T> fromNet) implements CustomIngredientSerializer<T> {
	@Override
	public ResourceLocation getIdentifier() {
		return id;
	}

	@Override
	public Codec<T> getCodec(boolean allowEmpty) {
		return codec;
	}

	@Override
	public T read(FriendlyByteBuf buf) {
		return fromNet.apply(buf);
	}

	@Override
	public void write(FriendlyByteBuf buf, T ingredient) {
		ingredient.write(buf);
	}
}
