package dev.latvian.mods.kubejs.item.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ModIngredient extends Ingredient {
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
		super(Stream.empty());
		this.mod = mod;
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && stack.kjs$getMod().equals(mod);
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", "kubejs:mod");
		json.addProperty("mod", mod);
		return json;
	}
}
