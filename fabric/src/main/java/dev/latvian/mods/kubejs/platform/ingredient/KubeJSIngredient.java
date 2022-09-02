package dev.latvian.mods.kubejs.platform.ingredient;

import com.faux.ingredientextension.api.ingredient.IngredientExtendable;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.core.IngredientKJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientPlatformHelper;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import java.util.stream.Stream;

public abstract class KubeJSIngredient extends IngredientExtendable implements IngredientKJS {
	public KubeJSIngredient() {
		super(Stream.empty());
		values = IngredientPlatformHelper.EMPTY_VALUES;
		itemStacks = ItemStackJS.EMPTY_ARRAY;
	}

	@Override
	public ItemStack[] getItems() {
		if (itemStacks == null) {
			dissolve();
		}

		return itemStacks;
	}

	@Override
	public IntList getStackingIds() {
		if (stackingIds == null) {
		}

		return stackingIds;
	}

	@Override
	public void dissolve() {
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	public abstract void toJson(JsonObject json);

	public abstract void write(FriendlyByteBuf buf);
}
