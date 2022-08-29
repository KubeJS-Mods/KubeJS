package dev.latvian.mods.kubejs.platform.ingredient;

import com.faux.ingredientextension.api.ingredient.serializer.IIngredientSerializer;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.core.ItemStackKJS;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ModIngredient extends KubeJSIngredient {
	public static final KubeJSIngredientSerializer<ModIngredient> SERIALIZER = new KubeJSIngredientSerializer<>(ModIngredient::ofModFromJson, ModIngredient::ofModFromNetwork);

	private static final Map<String, ModIngredient> CACHE = new HashMap<>();

	public static ModIngredient ofMod(String mod) {
		return CACHE.computeIfAbsent(mod, ModIngredient::new);
	}

	public static ModIngredient ofModFromNetwork(FriendlyByteBuf buf) {
		return ofMod(buf.readUtf());
	}

	public static ModIngredient ofModFromJson(JsonObject json) {
		return ofMod(json.get("mod").getAsString());
	}

	public final String mod;

	private ModIngredient(String mod) {
		this.mod = mod;
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && ((ItemStackKJS) (Object) stack).kjs$getMod().equals(mod);
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
