package dev.latvian.mods.kubejs.platform.ingredient;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.core.IngredientKJS;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;

import java.util.stream.Stream;

public abstract class KubeJSIngredient extends AbstractIngredient implements IngredientKJS {
	private static final Ingredient.Value[] EMPTY_VALUES = new Ingredient.Value[0];

	public KubeJSIngredient() {
		super(Stream.empty());
		values = EMPTY_VALUES;
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

	@Override
	public final boolean isSimple() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public final JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", CraftingHelper.getID(getSerializer()).toString());
		toJson(json);
		return json;
	}

	public abstract void toJson(JsonObject json);

	public abstract void write(FriendlyByteBuf buf);
}
