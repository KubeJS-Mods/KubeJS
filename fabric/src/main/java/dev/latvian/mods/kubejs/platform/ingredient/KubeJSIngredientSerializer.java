package dev.latvian.mods.kubejs.platform.ingredient;

import com.faux.ingredientextension.api.ingredient.serializer.IIngredientSerializer;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Function;

public record KubeJSIngredientSerializer<T extends KubeJSIngredient>(Function<JsonObject, T> fromJson, Function<FriendlyByteBuf, T> fromNet) implements IIngredientSerializer<T> {
	@Override
	public T fromNetwork(FriendlyByteBuf buf) {
		return fromNet.apply(buf);
	}

	@Override
	public T fromJson(JsonObject json) {
		return fromJson.apply(json);
	}

	@Override
	public void toJson(JsonObject json, T ingredient) {
		ingredient.toJson(json);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buf, T ingredient) {
		ingredient.write(buf);
	}
}
