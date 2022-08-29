package dev.latvian.mods.kubejs.platform.ingredient;

import com.faux.ingredientextension.api.ingredient.IngredientExtendable;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.core.IngredientKJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientPlatformHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import java.util.stream.Stream;

public abstract class KubeJSIngredient extends IngredientExtendable implements IngredientKJS {
	public KubeJSIngredient() {
		super(Stream.empty());
		values = IngredientPlatformHelper.EMPTY_VALUES;
	}

	@Override
	public ItemStack[] getItems() {
		if (itemStacks == null) {
			dissolve();
		}

		return itemStacks;
	}

	@Override
	public void dissolve() {
		if (itemStacks == null) {
			itemStacks = kjs$getStacks().toArray();
		}
	}

	public abstract void toJson(JsonObject json);

	public abstract void write(FriendlyByteBuf buf);
}
