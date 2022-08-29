package dev.latvian.mods.kubejs.platform.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.Nullable;

public class NotIngredient extends KubeJSIngredient {
	public static final KubeJSIngredientSerializer<NotIngredient> SERIALIZER = new KubeJSIngredientSerializer<>(NotIngredient::new, NotIngredient::new);

	public final Ingredient ingredient;

	public NotIngredient(Ingredient ingredient) {
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
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", "kubejs:not");
		json.add("ingredient", ingredient.toJson());
		return json;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		ingredient.toNetwork(buf);
	}

	@Override
	public Ingredient kjs$not() {
		return ingredient;
	}
}
