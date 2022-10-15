package dev.latvian.mods.kubejs.platform.fabric.ingredient;

import com.faux.ingredientextension.api.ingredient.serializer.IIngredientSerializer;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public class ModIngredient extends KubeJSIngredient {
	public static final KubeJSIngredientSerializer<ModIngredient> SERIALIZER = new KubeJSIngredientSerializer<>(ModIngredient::ofModFromJson, ModIngredient::ofModFromNetwork);

	public static ModIngredient ofModFromNetwork(FriendlyByteBuf buf) {
		return new ModIngredient(buf.readUtf());
	}

	public static ModIngredient ofModFromJson(JsonObject json) {
		return new ModIngredient(json.get("mod").getAsString());
	}

	public final String mod;

	public ModIngredient(String mod) {
		this.mod = mod;
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && stack.kjs$getMod().equals(mod);
	}

	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public void toJson(JsonObject json) {
		json.addProperty("mod", mod);
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeUtf(mod);
	}
}
