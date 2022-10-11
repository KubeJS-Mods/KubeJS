package dev.latvian.mods.kubejs.item.ingredient;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;

// WIP
public class WrappedIngredient {
	@FunctionalInterface
	public interface JsonFactory {
		WrappedIngredient fromJson(JsonObject json);
	}

	@FunctionalInterface
	public interface NetworkFactory {
		WrappedIngredient fromNetwork(FriendlyByteBuf buf);
	}
}
