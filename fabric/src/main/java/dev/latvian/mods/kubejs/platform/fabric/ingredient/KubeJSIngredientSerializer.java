package dev.latvian.mods.kubejs.platform.fabric.ingredient;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public record KubeJSIngredientSerializer<T extends KubeJSIngredient>(ResourceLocation id, Function<JsonObject, T> fromJson, Function<FriendlyByteBuf, T> fromNet) implements CustomIngredientSerializer<T> {
	@Override
	public ResourceLocation getIdentifier() {
		return id;
	}

	@Override
	public T read(JsonObject json) {
		return fromJson.apply(json);
	}

	@Override
	public void write(JsonObject json, T ingredient) {
		ingredient.toJson(json);
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
