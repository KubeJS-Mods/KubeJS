package dev.latvian.mods.kubejs.platform.ingredient;

import com.google.gson.JsonObject;
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
	public boolean test(@Nullable ItemStack stack) {
		return stack != null;
	}

	@Override
	public boolean isSimple() {
		return true;
	}

	@Override
	public boolean kjs$isWildcard() {
		return true;
	}

	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public JsonObject toJson() {
		var json = new JsonObject();
		json.addProperty("type", "kubejs:wildcard");
		return json;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
	}
}
