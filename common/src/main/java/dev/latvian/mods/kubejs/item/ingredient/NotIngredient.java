package dev.latvian.mods.kubejs.item.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class NotIngredient extends Ingredient {
	public final Ingredient ingredient;

	public NotIngredient(Ingredient ingredient) {
		super(Stream.empty());
		this.ingredient = ingredient;
	}

	public NotIngredient(FriendlyByteBuf buf) {
		this(Ingredient.fromNetwork(buf));
	}

	public NotIngredient(JsonObject json) {
		this(Ingredient.fromJson(json.get("ingredient")));
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && !ingredient.test(stack);
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", "kubejs:not");
		json.add("ingredient", ingredient.toJson());
		return json;
	}

	@Override
	public Ingredient kjs$not() {
		return ingredient;
	}
}
