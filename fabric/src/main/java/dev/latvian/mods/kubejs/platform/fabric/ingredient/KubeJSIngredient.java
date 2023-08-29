package dev.latvian.mods.kubejs.platform.fabric.ingredient;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.fabric.CustomIngredientKJS;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.minecraft.network.FriendlyByteBuf;

public abstract class KubeJSIngredient implements CustomIngredient, CustomIngredientKJS {
	@Override
	public boolean requiresTesting() {
		return false;
	}

	public abstract void toJson(JsonObject json);

	public abstract void write(FriendlyByteBuf buf);

	@Override
	public abstract KubeJSIngredientSerializer<?> getSerializer();

	@Override
	public boolean kjs$canBeUsedForMatching() {
		// like on forge, all of our ingredients should be safe for matching,
		// unless somebody does something *really* weird from scripts
		return true;
	}
}
