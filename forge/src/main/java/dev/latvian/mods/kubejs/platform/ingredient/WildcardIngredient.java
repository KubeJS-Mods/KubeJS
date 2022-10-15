package dev.latvian.mods.kubejs.platform.ingredient;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.Nullable;

public class WildcardIngredient extends KubeJSIngredient {
	public static WildcardIngredient INSTANCE = new WildcardIngredient();
	public static final KubeJSIngredientSerializer<WildcardIngredient> SERIALIZER = new KubeJSIngredientSerializer<>(json -> INSTANCE, buf -> INSTANCE);

	private WildcardIngredient() {
	}

	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null;
	}

	@Override
	public boolean kjs$isWildcard() {
		return true;
	}

	@Override
	public void dissolve() {
		if (this.itemStacks == null) {
			this.itemStacks = ItemStackJS.getList().toArray(new ItemStack[0]);
		}
	}

	@Override
	public void toJson(JsonObject json) {
	}

	@Override
	public void write(FriendlyByteBuf buf) {
	}
}
