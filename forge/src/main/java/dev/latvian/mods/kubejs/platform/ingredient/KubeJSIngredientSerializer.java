package dev.latvian.mods.kubejs.platform.ingredient;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import java.util.function.Function;

public record KubeJSIngredientSerializer<T extends KubeJSIngredient>(Function<JsonObject, T> fromJson, Function<FriendlyByteBuf, T> fromNet) implements IIngredientSerializer<T> {
	@Override
	public T parse(JsonObject json) {
		return fromJson.apply(json);
	}

	@Override
	public T parse(FriendlyByteBuf buf) {
		return fromNet.apply(buf);
	}

	@Override
	public void write(FriendlyByteBuf buf, T ingredient) {
		ingredient.write(buf);
	}
}
