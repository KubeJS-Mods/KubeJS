package dev.latvian.mods.kubejs.platform.fabric.ingredient;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.minecraft.network.FriendlyByteBuf;

public abstract class KubeJSIngredient implements CustomIngredient {
	@Override
	public boolean requiresTesting() {
		return false;
	}

	public abstract void toJson(JsonObject json);

	public abstract void write(FriendlyByteBuf buf);

	@Override
	public abstract KubeJSIngredientSerializer<?> getSerializer();
}
