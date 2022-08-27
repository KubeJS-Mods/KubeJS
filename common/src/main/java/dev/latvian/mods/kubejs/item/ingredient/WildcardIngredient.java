package dev.latvian.mods.kubejs.item.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class WildcardIngredient extends Ingredient {
	public static final WildcardIngredient INSTANCE = new WildcardIngredient();

	private WildcardIngredient() {
		super(Stream.empty());
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", "kubejs:wildcard");
		return json;
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null;
	}
}
